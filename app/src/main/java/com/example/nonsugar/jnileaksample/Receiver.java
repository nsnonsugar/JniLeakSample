package com.example.nonsugar.jnileaksample;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;


class Receiver {
    private static final String TAG = "Receiver";
    private static final ConcurrentLinkedQueue<BigObject> queue = new ConcurrentLinkedQueue <>();


    static void startReceiverThread() {
        new Thread(new Runnable() {
            @Override
            @SuppressWarnings("InfiniteLoopStatement")
            public void run() {
                while (true) {
                    BigObject obj = dequeue();
                    Log.d(TAG, String.valueOf(obj.getBuffer().length));
                }
            }
        }).start();
    }

    /**
     * 0.1秒ごとにReceiverThreadにBigObjectを渡すスレッド
     */
    static void starSenderThread() {
        new Thread(new Runnable() {
            @Override
            @SuppressWarnings("InfiniteLoopStatement")
            public void run() {
                while (true) {
                    BigObject obj = new BigObject();
                    Receiver.enqueue(obj);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }


                }
            }
        }).start();
    }

    /**
     * ReceiverThreadのキューにBigObjectを入れる
     */
    static void enqueue(BigObject obj) {
        synchronized (queue) {
            queue.add(obj);
            queue.notify();
        }
    }

    /**
     * キューからデータを取り出す
     */
    private static BigObject dequeue() {
        synchronized (queue) {
            while (queue.isEmpty()) {
                try {
                    queue.wait();
                } catch (InterruptedException ignored) {
                }
            }
        }

        return queue.poll();
    }

}
