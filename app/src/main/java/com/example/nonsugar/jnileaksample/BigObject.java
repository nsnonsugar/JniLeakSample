package com.example.nonsugar.jnileaksample;

class BigObject {
    private byte[] mBuf;

    BigObject() {
        // とりあえず1MB程度確保しておく
        mBuf = new byte[1048576];
    }


    byte[] getBuffer() {
        return mBuf;
    }
}
