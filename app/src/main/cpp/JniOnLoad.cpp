#include <jni.h>
#include <thread>

extern "C" {
#include "unistd.h"
}

JavaVM* gJavaVM = nullptr;
JNIEnv* gEnv = nullptr;

// クラスへのグローバル参照
jclass gReceiver;
jclass gBigObject;

// メソッドIDキャッシュ
jmethodID gBigObjectCtor;
jmethodID gReceiverEnqueue;

extern "C" jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
    static_cast<void>(reserved);

    gJavaVM = vm;
    JNIEnv* env = nullptr;
    gJavaVM->AttachCurrentThread(&env, nullptr);

    // クラスのグローバル参照を取得
    jclass cls = env->FindClass("com/example/nonsugar/jnileaksample/Receiver");
    gReceiver = reinterpret_cast<jclass>(env->NewGlobalRef(cls));

    cls = env->FindClass("com/example/nonsugar/jnileaksample/BigObject");
    gBigObject = reinterpret_cast<jclass>(env->NewGlobalRef(cls));

    // メソッドIDもここでキャッシュしておく
    gBigObjectCtor = env->GetMethodID(gBigObject, "<init>", "()V");
    gReceiverEnqueue = env->GetStaticMethodID(gReceiver, "enqueue", "(Lcom/example/nonsugar/jnileaksample/BigObject;)V");


    return JNI_VERSION_1_6;
}

void sendJava()
{
    // BigObject生成
    jobject bigObject = gEnv->NewObject(gBigObject, gBigObjectCtor);

    // Receiver#enqueuを呼び出す
    gEnv->CallStaticVoidMethod(gReceiver, gReceiverEnqueue, bigObject);

    gEnv->DeleteLocalRef(bigObject);
}

void nativeWorker()
{
    gJavaVM->AttachCurrentThread(&gEnv, nullptr);
    while (true) {
        sendJava();
        usleep(100000);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_nonsugar_jnileaksample_MainActivity_startNativeThread(JNIEnv *env, jobject /* this */)
{
    std::thread th(nativeWorker);
    th.join();
}


