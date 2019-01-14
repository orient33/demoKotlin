package com.example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

class RxJavaDemo {
    static void test() {
        Disposable d = Observable.create(emitter -> {
            int i = 1 + 2;
            log("12. subscribe: " + emitter);
        })
                .subscribe((result) -> {
                    log("on result : " + result);
                }, e -> {
                    log("on error : " + e);
                });
        d = Flowable.create(e -> log("19.emitter.!!"), BackpressureStrategy.LATEST).subscribe(r -> {
            log("Flowable complete ");
        });
        Observable<String> o = Observable.create(e -> {
            log("observable. not .. is subscribe?");
            e.onNext("1");
            e.onComplete();
//            e.onError(new RuntimeException("----------"));
        });

        String v = o.blockingFirst();
        log("block first : " + v);

        Flowable.create(e -> {
            e.onNext("a");
            e.onNext("b");
            e.onComplete();
        }, BackpressureStrategy.LATEST)
                .subscribe(r -> {
                    log(" consumer " + r);
                })
                .isDisposed();


        Single<String> s = Single.<String>create(emitter -> {
            try {
                synchronized (emitter) {
                    emitter.wait(7100);
                }
            } catch (InterruptedException e) {
                log("interrupted!");
            }
            emitter.onSuccess("tt");
//            emitter.onError(new RuntimeException("22"));
        }).timeout(6, TimeUnit.SECONDS, Schedulers.computation());
        try {
            log("Single block get: " + s.blockingGet());
        } catch (RuntimeException e) {
            log("Single block . time out. " + e);
        }
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    private static void log(String s) {
        System.out.println(sdf.format(new Date()) + ". RxJavaDemo: " + s);
    }
}
