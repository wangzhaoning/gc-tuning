# GC调优实战：减少Full GC的次数

在我们的一个应用中，由于场景特殊（需要在内存开辟较大的临时空间用于计算），
应用会出现频繁的老年代GC和Full GC。请根据具体的业务场景调整[这里](https://github.com/hcsp/gc-tuning/blob/master/src/test/java/com/github/hcsp/GcTest.java)的JVM启动参数，
使得测试中老年代GC/Full GC出现的次数小于3次。

在提交Pull Request之前，你应当在本地确保所有代码已经编译通过，并且通过了测试(`mvn clean test`)

-----
注意！我们只允许你修改以下文件，对其他文件的修改会被拒绝：
- [src/test/java/com/github/hcsp/GcTest.java](https://github.com/hcsp/gc-tuning/blob/master/src/test/java/com/github/hcsp/GcTest.java)
-----

