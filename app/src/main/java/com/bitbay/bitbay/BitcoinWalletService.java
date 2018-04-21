package com.bitbay.bitbay;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
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
import java.util.concurrent.CountDownLatch;

import static com.google.android.gms.common.internal.zzbq.checkNotNull;
import static com.google.android.gms.internal.zzagr.runOnUiThread;

public class BitcoinWalletService extends Service {
    WalletAppKit mWalletAppKit;
    NetworkParameters mNetworkParameters;
    static Context context;
    String FILE_PREFIX = "forwarding-service-testnet";
    String FILES_DIR;

    CountDownLatch countDownLatch;

    String LOG_TAG = "BitcoinActivity";

    public BitcoinWalletService() {
        FILES_DIR = context.getFilesDir().getPath().toString();
    }

    public class BitcoinWalletBinder extends Binder {
        public BitcoinWalletService getService() {
            return BitcoinWalletService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        countDownLatch = new CountDownLatch(2);
        Runnable createWalletObjectAsync = new Runnable() {
            @Override
            public void run() {
                mNetworkParameters = TestNet3Params.get();
                File dir = new File(FILES_DIR);
                WalletAppKit kit = new WalletAppKit(mNetworkParameters, dir, FILE_PREFIX){
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
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "Coins were received!",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        Log.i(LOG_TAG,"Confirmation received.");
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Wallet is ready!",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

                    }
                };

                kit.startAsync();
//                try {
//                    countDownLatch.await();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                kit.awaitRunning();
//                Log.i("BitcoinActivity", "Creating wallet: passed awaitRunning");
            }
        };
        new Thread(createWalletObjectAsync).start();

        return START_NOT_STICKY;
    }

    public void pay(String recipientPublicKey, long priceInSatoshis) throws Exception {
        try {
            Address recipientAddress = Address.fromBase58(mNetworkParameters, recipientPublicKey);
            // Now send the coins onwards.

            SendRequest sendRequest = SendRequest.to(recipientAddress, Coin.valueOf(priceInSatoshis));
            final Wallet.SendResult sendResult = mWalletAppKit.wallet().sendCoins(sendRequest);
            checkNotNull(sendResult);  // We should never try to send more coins than we have!
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Sending ...", Toast.LENGTH_LONG).show();
                }
            });
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
                            Toast.makeText(getApplicationContext(),
                                    "Sent coins onwards! Transaction hash is " +
                                            sendResult.tx.getHashAsString(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }, MoreExecutors.sameThreadExecutor()); // directExecuter ??
        } catch (KeyCrypterException | InsufficientMoneyException e) {
            // We don't use encrypted wallets in this example - can never happen.
            throw new Exception(e);
        }
    }

    private void showReadyToast()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Wallet is ready for action!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showNotReadyToast()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Wallet not ready", Toast.LENGTH_LONG).show();
            }
        });
    }

    public long getBalance()
    {
        if (mWalletAppKit == null) {
            showNotReadyToast();
            return -1;
        }
        return mWalletAppKit.wallet().getBalance().getValue();
    }

    public Address getCurrentAddress()
    {
        if (mWalletAppKit == null)
        {
            showNotReadyToast();
            return null;
        }
        return mWalletAppKit.wallet().currentReceiveAddress();
    }

    public static void setContext(Context _context)
    {
        context = _context;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new BitcoinWalletBinder();
    }
}

