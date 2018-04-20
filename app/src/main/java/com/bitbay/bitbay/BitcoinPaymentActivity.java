package com.bitbay.bitbay;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BitcoinPaymentActivity extends AppCompatActivity {

    public WalletAppKit kit;
    private NetworkParameters params;
    private Address forwardingAddress;
    private String filePrefix;

    final Handler handler = new Handler();
    private String userKey;
    private EditText mEdit;

    //HARDCODED
    private double price = 0.000000000001;
    private String recipient = "mkVm2g34Mq5Kqx7AWKoMe2ARzotWTM75ov";
    public TextView mText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin_payment);
        mText = (TextView) findViewById(R.id.statusBit);
//        get price from prev activity
//        Intent intent = getIntent();
//        Bundle bundle = intent.getExtras();
//        price = (int) bundle.get("price");
        mEdit = (EditText) findViewById(R.id.userKey);
        createForwardingAddress();
        createNewWallet();

    }

    public void getKey(View view){
        userKey = mEdit.getText().toString();
        mEdit.setText("");
    }

    public void createForwardingAddress(){
        String network = "testnet";
        params = TestNet3Params.get();
        filePrefix = "forwarding-service-testnet";
        forwardingAddress = Address.fromBase58(params, recipient);
        mText.setText("finish step: create forwarding address");
    }

    class AsyncWallet extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            File file = new File(getApplicationContext().getFilesDir().getPath().toString());
            kit = new WalletAppKit(params, file, filePrefix) {
                @Override
                protected void onSetupCompleted() {
                    if (wallet().getKeyChainGroupSize() < 1)
                        wallet().importKey(new ECKey());
                }


            };
//        if (params == RegTestParams.get()) {
//            kit.connectToLocalHost();
//        }
            mText.setText("finish step: create wallet");
            // Download the block chain and wait until it's done.
            kit.startAsync();
            kit.awaitRunning();
            mText.setText("finish step: Download the block chain");
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            walletListeners();
        }
    }
    public void createNewWallet() {
        new AsyncWallet().execute();
    }

    void walletListeners() {
        kit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin
                    newBalance) {
                // Runs in the dedicated "user thread".
            }
        });
        kit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin
                    newBalance) {
                Coin value = tx.getValueSentToMe(w);
                System.out.println("Received tx for " + value.toFriendlyString() + ": " + tx);
                System.out.println("Transaction will be forwarded after it confirms.");

                Futures.addCallback(tx.getConfidence().getDepthFuture(1), new
                        FutureCallback<TransactionConfidence>() {
                            @Override
                            public void onSuccess(TransactionConfidence result) {
                                forwardCoins(result);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                            }
                        });
            }
        });

    }

    public void forwardCoins(TransactionConfidence tx) {

    }

    /***
     * Then we just invoke a method we define ourselves called forwardCoins when the transaction
     * that sends us money confirms.
     * @param tx
     */
    public void sendCoins(Transaction tx) throws InsufficientMoneyException {
        Coin value = tx.getValueSentToMe(kit.wallet());
        System.out.println("Forwarding " + value.toFriendlyString() + " BTC");
        final Coin amountToSend = value.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
        final Wallet.SendResult sendResult = kit.wallet().sendCoins(kit.peerGroup(),
                forwardingAddress, amountToSend);
        System.out.println("Sending ...");

        Runnable run = new Runnable() {
            @Override
            public void run() {
                System.out.println("Sent coins onwards! Transaction hash is " + sendResult.tx
                        .getHashAsString());
            }
        } ;
        sendResult.broadcastComplete.addListener(run, Executors.newSingleThreadExecutor());
    }


    public Transaction createTransaction(Address address, Coin value) throws
            InsufficientMoneyException {
        SendRequest req = SendRequest.to(address, value);
        req.feePerKb = Coin.parseCoin("0.0005");
        Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), req);
        Transaction createdTx = result.tx;
        return createdTx;
    }
}


//https://www.javaworld.com/article/2078482/java-web-development/bitcoin-for-beginners--part-3--the-bitcoinj-api.html