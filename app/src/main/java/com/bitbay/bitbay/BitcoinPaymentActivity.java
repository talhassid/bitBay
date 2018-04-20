package com.bitbay.bitbay;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import static com.google.android.gms.common.internal.zzbq.checkNotNull;


public class BitcoinPaymentActivity extends AppCompatActivity {

    //HARDCODED
    private final double price = 0.000000000001;
    private final String MY_RECIPIENT_TEXTUAL_PUBLIC_KEY = "n3APWezT42i6bGB6NG3MQ9RTxTCtpFugqx";
    final String LOG_TAG = "BitcoinActivity";

    Address mForwardingAddress; // the address that the payment will be forwarded to
    WalletAppKit mWalletAppKit; // a bundle for all of the wallet factors
    NetworkParameters mNetworkParameters; // define what type of network we run on (test / production)
    Address mMyWalletAddress; // This application saved on device wallet
    String filePrefix = "forwarding-service-testnet";

    TextView mSendToTextView;
    TextView mTransactionResultTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin_payment);

        mSendToTextView = findViewById(R.id.tv_sendto_address);
        mTransactionResultTextView = findViewById(R.id.tv_transaction_result);

        mNetworkParameters = TestNet3Params.get();
        mForwardingAddress = Address.fromBase58(mNetworkParameters, MY_RECIPIENT_TEXTUAL_PUBLIC_KEY);
        Log.i("BitcoinActivity", "Forwarding Address: " + mForwardingAddress);
        Executors.newSingleThreadExecutor().execute(new CreateWalletAsyncTask());
    }

    private class CreateWalletAsyncTask implements Runnable {
        @Override
        public void run() {
            Log.i("BitcoinActivity", "Creating wallet " + mForwardingAddress);
            File file = new File(getApplicationContext().getFilesDir().getPath().toString());
            WalletAppKit kit = new WalletAppKit(mNetworkParameters, file, filePrefix){
                // This is called in a background thread after startAndWait is called, as setting up various objects
                // can do disk and network IO that may cause UI jank/stuttering in wallet apps if it were to be done
                // on the main thread.
                @Override
                protected void onSetupCompleted() {
                    Log.i("BitcoinActivity", "Creating wallet: setup complete");
                    if (wallet().getKeyChainGroupSize() < 1) {
                        ECKey eckey = new ECKey();
                        wallet().importKey(eckey);
                        Log.i("BitcoinActivity",
                                "Creating wallet: Created a new ECKey: " + eckey);
                    }

                    // We want to know when we receive money.
                    wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
                        @Override
                        public void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
                            // Runs in the dedicated "user thread" (see bitcoinj docs for more info on this).
                            //
                            // The transaction "tx" can either be pending, or included into a block (we didn't see the broadcast).
                            Coin value = tx.getValueSentToMe(w);
                            Log.i(LOG_TAG, "Received tx for " + value.toFriendlyString() + ": " + tx);
                            Log.i(LOG_TAG, "Transaction will be forwarded after it confirms.");
                            // Wait until it's made it into the block chain (may run immediately if it's already there).
                            //
                            // For this dummy app of course, we could just forward the unconfirmed transaction. If it were
                            // to be double spent, no harm done. Wallet.allowSpendingUnconfirmedTransactions() would have to
                            // be called in onSetupCompleted() above. But we don't do that here to demonstrate the more common
                            // case of waiting for a block.
                            final Transaction finalTx = tx;
                            Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
                                @Override
                                public void onSuccess(TransactionConfidence result) {
                                    System.out.println("Confirmation received.");
                                    forwardCoins(finalTx);
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    // This kind of future can't fail, just rethrow in case something weird happens.
                                    throw new RuntimeException(t);
                                }
                            });
                        }
                    });


                    mWalletAppKit = this;
                    Log.i("BitcoinActivity", "Creating wallet: done! and will forward to:" + mForwardingAddress);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("BitcoinActivity", "send coins to:" + mWalletAppKit.wallet().currentReceiveAddress());
                            mSendToTextView.setText("send coins to: " + mWalletAppKit.wallet().currentReceiveAddress());
                        }
                    });
                }
            };
            // Download the block chain and wait until it's done.
            kit.startAsync();
        }
    }

    private void forwardCoins(Transaction tx)
    {
        try {
            // Now send the coins onwards.
            SendRequest sendRequest = SendRequest.emptyWallet(mForwardingAddress);
            final Wallet.SendResult sendResult = mWalletAppKit.wallet().sendCoins(sendRequest);
            checkNotNull(sendResult);  // We should never try to send more coins than we have!
            mTransactionResultTextView.setText("Sending ...");
            // Register a callback that is invoked when the transaction has propagated across the network.
            // This shows a second style of registering ListenableFuture callbacks, it works when you don't
            // need access to the object the future returns.
            sendResult.broadcastComplete.addListener(new Runnable() {
                @Override
                public void run() {
                    // The wallet has changed now, it'll get auto saved shortly or when the app shuts down.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTransactionResultTextView.setText(
                                    "Sent coins onwards! Transaction hash is " +
                                            sendResult.tx.getHashAsString());
                        }
                    });
                }
            }, MoreExecutors.sameThreadExecutor()); // directExecuter ??
        } catch (KeyCrypterException | InsufficientMoneyException e) {
            // We don't use encrypted wallets in this example - can never happen.
            throw new RuntimeException(e);
        }
    }
}






















