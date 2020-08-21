## java 内存区域

### 运行时数据区域

![](https://oscimg.oschina.net/oscnet/up-a4f8299e9df48942fbc1bd6ec704ce9da0d.png)

##### 程序计数器
* 一块较小的内存空间，看作是当前线程所执行的字节码的行号指示器。
* Java 多线程是通过线程轮流切换并分配处理器执行时间来实现的，因此，每条线程都有独立的程序计数器，是线程私有的内存。
* 若线程在执行一个java 方法，则计数器记录的是字节码指令的地址；若是本地Native 方法，则计数器为空。
* 此区域是唯一一个在java 虚拟机规范中没有规定任何 OutOfMemoryError 情况的区域

##### Java 虚拟机栈
* 线程私有的，它的生命周期与线程相同
* 虚拟机栈描述的是Java 方法执行的内存模型：
    * 每个方法在执行的同时都会创建一个栈帧用于存储局部变量表、操作数栈、动态链接、方法出口等信息
    * 每个方法从调用直到执行完成的过程，就对应一个栈帧在虚拟机栈中入栈到出栈的过程
* 通常说的堆内存Heap 和栈内存Stack，栈指的就是虚拟机栈中的局部变量表
* 局部变量表存放了编译期可知的各种数据类型（boolean、byte、char、short、int、float、long、double）、对象引用（reference 对象）、returnAddress 类型（指向了一条字节码指令的地址）
* 局部变量表所需的内存空间在编译期间完成分配，一个方法在栈帧中分配多大的局部变量空间是完全确定的
    * 64位长度的long 和double 类型的数据占用两个局部变量空间（Slot）
    * 其他数据类型占用一个Slot
* 该区域规定了两种异常状态：
    * 如果线程请求的栈深度大于虚拟机所允许的深度，抛出 StackOverflowError 异常
    * 如果虚拟机栈无法申请到足够的内存，抛出 OutOfMemoryError 异常

##### 本地方法栈
* 本地方法栈与虚拟机栈相似，只不过为虚拟机使用到的Native 方法服务
* HotSpot 虚拟机将本地方法栈和虚拟机栈合二为一
* 也会抛出StackOverflowError 和 OutOfMemoryError 异常

##### Java 堆
* 内存中最大的一块，并且是被所有线程共享的一块内存区域，在虚拟机启动时创建
* 存放几乎所有的对象实例，JIT 编译器与逃逸分析技术的发展，栈上分配、标量替换让所有对象都在堆上分配变得不那么绝对了
* 是垃圾收集器管理的主要区域
* 一般是可扩展的，通过-Xms 和-Xmx 控制，无法再扩展时，抛出 OutOfMemoryError 异常

##### 方法区
* 线程共享的内存区域，用于存储已被虚拟机加载的类信息、常量、静态变量、即时编译器编译的代码等数据
* 无法满足内存需求时，抛出 OutOfMemoryError 异常

##### 运行时常量池
* 是方法区的一部分，Class 文件有一项信息是常量池，用于存放编译期生成的各种字面量和符号引用，在类加载后进入运行时常量池
* 运行时常量池相对于Class 文件常量池是具备动态性，允许运行期间将新的常量放入池中，例如 String 的intern() 方法
* 也会抛出 OutOfMemoryError 异常

##### 直接内存
* 并不是虚拟机运行时的数据区的一部分，也不是规范中定义的内存区域
* 它使用Native 函数库直接分配堆外内存，通过存储在堆中的DirectByteBuffer 对象作为这块内存的引用，避免了在Java 堆和Native 堆中来回复制数据
* 不受Java 堆大小的限制，受本机总内存大小以及处理器寻址空间的限制
* 也会抛出 OutOfMemoryError 异常


### 虚拟机对象

##### 创建对象

![](https://oscimg.oschina.net/oscnet/up-f2c422c3e896da4bf478e9e9debe4252e36.png)

内存分配的方式：
* 指针碰撞 Bump the Pointer
    * 若Java 堆内存是绝对规整的，用过的内存在一边，空闲的内存在另一边，中间放着指针作为分界点的指示器
    * 分配内存仅仅是把那个指针向空闲空间那边挪动一段与对象大小相等的距离，这种分配方式称为 指针碰撞
* 空闲列表 Free List
    * 若Java 内存并不是规整的，已使用的内存和空闲的内存相互交错
    * 需要维护一个列表，记录哪块内存是可用的，在分配的时候从列表中找一块足够大的空间划分给对象实例，并更新列表上的记录

内存分配可能存在线程安全问题，解决方案：
* 对分配内存空间的操作进行同步处理，虚拟机采用CAS 配上失败重试的方式保证更新操作的原子性
* 把内存分配的动作按照线程划分在不同的空间之中进行
    * 每个Java 线程先预先分配一小块内存，称为本地线程分配缓冲TLAB
    * 分配内存时，先在线程的TLAB 上分配，TLAB 用完并分配新的TLAB 时才需要同步锁定
    * -XX:+/-UseTLAB 参数来设定是否启用


##### 对象的内存布局

* 对象头 Header
    * Mark Word
        * 存储对象自身的运行数据，如哈希码、GC 分代年龄、锁状态标志、线程持有的锁、偏向线程ID、偏向时间戳等
        * 32位虚拟机的数据长度为32bit；64位虚拟机的数据长度为64bit，若开启指针压缩，将节约50%的内存，+UseCompressedOops 用来开启指针压缩
        * 32bit 的空间，25bit 存储对象哈希码，4bit 存储对象分代年龄，2bit 存储锁标志位，1bit 固定为0
        ```
        |-------------------------------------------------------|--------------------|
        |                  Mark Word (32 bits)                  |       State        |
        |-------------------------------------------------------|--------------------|
        | identity_hashcode:25 | age:4 | biased_lock:1 | lock:2 |       Normal       |
        |-------------------------------------------------------|--------------------|
        |  thread:23 | epoch:2 | age:4 | biased_lock:1 | lock:2 |       Biased       |
        |-------------------------------------------------------|--------------------|
        |               ptr_to_lock_record:30          | lock:2 | Lightweight Locked |
        |-------------------------------------------------------|--------------------|
        |               ptr_to_heavyweight_monitor:30  | lock:2 | Heavyweight Locked |
        |-------------------------------------------------------|--------------------|
        |                                              | lock:2 |    Marked for GC   |
        |-------------------------------------------------------|--------------------|
        ```
    * 类型指针，即对象指向它的类元数据的指针，通过这个指针来确定对象是哪个类的实例
    * 若对象是Java 数组，则还需有一块记录数组长度的数据
* 实例数据 Instance Data
    * 存储对象真正的有效信息，包括父类继承下来的
* 对齐填充 Padding
    * 并不是必然存在的，仅仅起占位符的作用，HotSpot 要求对象的大小必须是8的倍数，需要通过对齐来补全


##### 对象的访问定位
通过栈上的reference 数据来操作堆上的具体对象，有两种对象访问方式：
* 使用句柄访问，reference 存储的是对象的句柄地址，堆中会划分一块内存作为句柄池，句柄包含了对象的实例数据与类型数据的地址信息
* 使用直接指针访问，reference 存储的是对象地址，HotSpot 使用的这种方式


## 垃圾收集器与内存分配策略

### 判断对象已死

##### 引用计数算法

基本思想是，给对象添加一个引用计数器，每当有一个地方引用它，计数器加1；引用失效时，计数器减1；计数器为0的对象就是不再被使用的

担忧一个问题，很难解决对象之间循环引用的问题。
例如：对象A 和对象B 都有字段instance，并互相引用对方的实例，除此之外，再无其他引用，这会导致两个对象的引用计数器都不为0

##### 可达性分析算法

基本思想是，通过一系列称为"GC roots"的对象作为起始点，从这些节点开始向下搜索，搜索所走过的路径为引用链，当一个对象到GC roots 没有任何引用链时，证明此对象不可用

在Java 中，GC roots 对象包含：
* 虚拟机栈（栈帧中的本地变量表）中引用的对象
* 方法区中类静态属性引用的对象
* 方法区中常量引用的对象
* 本地方法栈中JNI 引用的对象

##### 对象引用
* 强引用 Strong Reference：
    * 代码中普遍存在的，例如 Object obj = new Object()，只要强引用还存在，GC 就不会回收掉被引用的对象
* 软引用 Soft Reference：
    * 用来描述一些还有用但并非必须的对象，对于软引用的对象，在内存发生溢出之前，会被回收
* 弱引用 Weak Reference：
    * 用来描述非必须对象的，但强度比软引用弱，对于软引用的对象，只能存活到下一次GC 之前
    * 当GC 时，不论内存是否足够，该对象都会被回收
* 虚引用 Phantom Reference：
    * 是最弱的一种引用关系，对于虚引用的对象，不会影响其生存时间，也无法通过虚引用来获取一个对象实例
    * 虚引用关联的唯一目的是能在这个对象被GC 时收到一个系统通知

##### 回收方法区
* 一般不需要回收，而且在方法区中进行GC 的性价比比较低。
* 主要回收两部分内容：废弃常量和无用的类。回收废弃常量与回收堆中的对象类似
* 判断无用的类就比较苛刻，需满足三个条件：
    * 该类所有的实例都已被回收
    * 加载该类的ClassLoader 已被回收
    * 该类对应的java.lang.Class 对象没有在任何地方被引用，无法通过反射访问该类的方法
* 是否对类进行回收，使用 -Xnoclassgc 参数进行控制

### 垃圾收集算法

##### 标记-清除算法 Mark-Sweep
最基础的收集算法，分为标记和清除两个阶段：首先标记所有需要回收的对象，然后统一回收所有被标记的对象

*不足*
* 一是效率问题，标记和清除两个过程效率都不高；
* 二是空间问题，清除之后会有大量不连续的内存碎片，碎片太多可能会导致以后分配较大对象时，没有足够的连续内存分配而不得不提前触发一次GC

##### 复制算法
将内存按容量划分为大小相等的两块，每次只使用其中一块。当一块内存用完了，就将存活的对象复制到另一块上，再把已使用的内存空间一次性清除。

这样每次都对半区进行内存回收，内存分配是也不用考虑碎片的问题，只需要复制时在堆顶按顺序分配即可。代价就是内存缩小为原来的一半。

**一般使用这种收集算法回收新生代**，因为新生代的对象98%都是朝生夕死的，内存分配可以不按照 1：1 的比例划分，
将内存分为一块较大的Eden 空间和两块较小的Survivor 空间，每次使用Eden 和一块Survivor。
当回收时，将Eden 和Survivor 中还存活的对象一次性地复制到另一块Survivor 上，然后清理掉Eden 和刚才用过的Survivor 空间。
HotSpot 默认Eden 和Survivor 的比例是 8：1

当另一块Survivor 空间不足时，这些对象能够通过担保机制进入老年代

##### 标记-整理算法 Mark-Compact
**老年代一般使用这种算法**，先标记所有需要回收的对象，然后让存活对象向一端移动，然后直接清理掉端边界以外的对象


### 垃圾收集器

#### Serial 收集器
最基本的收集器，单线程的收集器，在进行垃圾收集时，必须暂停其他所有的工作线程，直到收集结束，这就是"Stop The World"。
对于限定的单CPU 环境，Serial 由于没有线程交互的开销，做垃圾收集有最高的单线程收集效率。

新生代采取复制算法，老年代采取标记-整理算法，暂停所有用户线程。

Serial 收集器对于运行在Client 模式下的虚拟机，是一个很好的选择，因为虚拟机内存不会很大，停顿时间完全可以控制在短时间内。

GC 日志关键字：DefNew(Default New Generation)

![](https://oscimg.oschina.net/oscnet/up-5c5793d875d49123b10885f40a52f8a458e.png)

#### ParNew 收集器

是Serial 收集器的多线程版本，除了使用多线程进行垃圾收集之外，其余行为包括Serial 收集器可用的所有控制参数（-XX:SurvivorRatio、-XX:PretenureSizeThreshold、
-XX:HandlePromotionFailure 等）、收集算法、Stop The World、对象分配规则、回收策略等都与Serial 一致

新生代采取复制算法，老年代采取标记-整理算法，暂停所有用户线程。

是运行在server 模式的虚拟机中首选的新生代收集器，可以与CMS 收集器配合工作。
当使用-XX:+UseConcMarkSweepGC 选项后，ParNew 是默认的新生代收集器，也可以使用-XX:+UseParNewGC 来强制指定。

GC日志关键字：ParNew(Parallel New Generation)

![](https://oscimg.oschina.net/oscnet/up-de27bd7b7f09a228d5c898869a1eb870d91.png)

#### Parallel Scavenge 收集器

也是新生代收集器，使用复制算法，并行的多线程收集器，但其关注点与其他收集器不同。

CMS 等收集器的关注点是尽可能缩短垃圾收集时用户线程的停顿时间，而Parallel Scavenge 收集器的目的是达到一个可控制的吞吐量。
吞吐量就是CPU 用于运行用户代码的时间与CPU 总消耗时间的比值，即吞吐量 = 运行用户代码的时间 / (运行用户代码时间 + 垃圾收集时间)。
停顿时间越短，越适合需要与用户交互的程序，而高吞吐量可以高效利用CPU 时间，主要适合在后台运算而不需要太多交互的任务。

-XX:MaxGCPauseMillis 控制最大垃圾收集停顿时间；-XX:GCTimeRatio 直接设置吞吐量大小

-XX:+UseAdaptiveSizePolicy 这是一个开关参数，当打开时，不需要指定新生代大小、Eden 和Survivor 的比例、晋升老年代对象年龄等细节参数，
虚拟机会根据运行情况动态调整这些参数，这种调节方式称为GC 自适应的调节策略。

GC日志关键字：PSYoungGen

![](https://oscimg.oschina.net/oscnet/up-5a8cf260e719e0a0c160379039c4268e443.png)

#### Serial Old 收集器

Serial Old 是Serial 的老年代版本，单线程收集器，使用标记-整理算法，主要意义在于给Client 模式下的虚拟机使用。

在server 模式下，可以在JDK 1.5 以及之前的版本中与Parallel Scavenge 收集器搭配使用；也可以作为CMS 收集器的后备预案

GC日志关键字：Tenured

#### Parallel Old 收集器

Parallel Scavenge 的老年版本，使用多线程和标记-整理算法。1.6之后才出现，吞吐量优先的收集器才有了应用组合（与Parallel Scavenge 组合使用）

GC日志关键字：ParOldGen

#### CMS 收集器 Concurrent Mark Sweep

是一种以获取最短回收停顿时间为目标的收集器，是基于标记-清除算法，整个过程分为四个步骤：
* 初始标记 CMS initial mark：Stop The World 仅仅标记一下GC Roots 能关联到的对象，时间很短
* 并发标记 CMS concurrent mark：进行GC Roots Tracing 的过程
* 重新标记 CMS remark：Stop The World 为了修正并发标记期间因用户程序运作而导致标记产生变动的那一部分标记记录，停顿时间比初始标记稍长
* 并发清除 CMS concurrent sweep

缺点：
* 因为并发，对CPU 资源非常敏感，会占用CPU 资源，导致总吞吐量降低
* 无法处理标记之后产生的垃圾，只好留到下一次gc，这部分称为浮动垃圾，可能会出现Concurrent Mode Failure 而导致一次full gc 的产生
* 基于标记-清除，那么会有空间碎片产生，默认在进行full gc时，进行碎片整理

![](https://oscimg.oschina.net/oscnet/up-2957cb6bca39310add6058ed4e5b52d1c9b.png)

#### G1 收集器

面向服务端的垃圾收集器，目标是多处理器机器、大内存机器，具备以下特点：
* 并行与并发
* 分代收集：可以独立处理不同代的对象，而不需要其他收集器配合
* 空间整合：整体来看是基于标记-整理算法，从局部（两个Refion 之间）来看是基于复制算法
* 可预测的停顿


G1 会记录回收所获的的空间大小以及回收所需的时间，下一次回收时根据允许的收集时间，优先回收价值最大的Region。
G1 中，Region 之间的对象引用，虚拟机使用Remembered Set 来避免全堆扫描。

G1 运作的大致步骤：
* 初始标记 Initial Marking：stop the world 短暂停顿来标记
* 并发标记 Concurrent Marking：并发标记
* 最终标记 Final Marking：stop the world 修正一下并发标记的结果
* 筛选回收 Live Data Counting and Evacuation：stop the world 会先对Region 的回收价值和成本进行排序，然后根据设定的GC 停顿时间来制定回收计划。

-XX:MaxGCPauseMillis 设定期望的GC 停顿时间，单位毫秒

![](https://oscimg.oschina.net/oscnet/up-2b2368be8f38888ada6bdcecc26e67e321e.png)

##### 内存区域

G1 将整个Java 堆划分为多个大小相等的独立区域Region，Region 默认大小为512k，逻辑上连续，物理内存地址不连续

![](https://oscimg.oschina.net/oscnet/up-b3c1c7defdd6c55cc9f0745df8c95c11bcf.png)

H 表示Humongous，表示大的对象，当分配的对象大于等于Region 大小的一半时，会被认为是巨型对象，默认分配在老年代，防止GC 时大对象的内存拷贝

##### 跨代引用

Young 区可能引用有Old 区的对象，这就是跨代引用，为了避免Young GC 时扫描整个老年代，G1 引入了Remembered Set 和Card Table。
* RSet：Remembered Set，用来记录外部指向本Region 的所有引用，每个Region 维护一个RSet
* Card：JVM 将每个Region 分成多个Card

![](https://oscimg.oschina.net/oscnet/up-19feb64fc888c947309bc22845beb9dfeda.png)
每个Region被分成了多个Card，其中绿色部分的Card表示该Card中有对象引用了其他Card中的对象，这种引用关系用蓝色实线表示。
RSet其实是一个HashTable，Key是Region的起始地址，Value是Card Table （字节数组）,字节数组下标表示Card的空间地址，当该地址空间被引用的时候会被标记为dirty_card。

##### G1 的GC 模式

* Young GC
    * Young GC 回收的是所有年轻代Region，当E区不能再分配新的对象时触发
    * E区的对象会移动到S区，当S区空间不足时，E区的对象会直接晋升到O区
    * S区的数据会移动到新的S区，如果S区的部分对象达到一定年龄，会晋升到O区
    
![](https://oscimg.oschina.net/oscnet/up-b6036dfb70dc42135edacfc21568fa124b3.png)
    
* Mixed GC
    * 回收所有年轻代Region 和部分老年代Region
    * -XX:MaxGCPauseMillis 指定G1 收集过程中期望的停顿时间，默认200ms。G1 会根据其停顿预测模型，挑选部分Region 进行回收，已满足停顿时间的需求
    * Mixed GC 的触发也由一些参数控制，-XX:InitiatingHeapOccupancyPercent 表示老年代占整个堆的百分比，默认45%，达到该阈值就会触发一次Mixed GC
* Mixed GC 主要分为两个阶段
    * 全局并发标记 global concurrent marking
        * 初始阶段 initial mark  STW：标记了从GC Root开始直接可达的对象。初始标记阶段借用young GC的暂停，因而没有额外的、单独的暂停阶段。
        * 并发标记 Concurrent Marking：这个阶段从GC Root开始对heap中的对象标记，标记线程与应用程序线程并行执行，并且收集各个Region的存活对象信息。
        * 最终标记 Remark  STW：标记那些在并发标记阶段发生变化的对象，将被回收。
        * 清除垃圾 Cleanup  部分STW：这个阶段如果发现完全没有活对象的region就会将其整体回收到可分配region列表中，清除空Region。
    * 拷贝存活对象 Evacuation
        * 该阶段是全暂停的，负责把一部分Region 中的存活对象拷贝到空的Region 中，回收原本的Region 空间
        * 该阶段可以自由选择任意多个Region 来收集构成收集集合（collection set，简称CSet），CSet集合中Region 的选定依赖于停顿预测模型
        * 该阶段并不回收所有有活对象的Region，只选择收益高的少量Region 来回收，这种暂停的开销就（在一定范围内）可控

![](https://oscimg.oschina.net/oscnet/up-7891b943aa797bc9f57320acb0fcf5c3c8f.png)

* Full GC
    * 由于垃圾回收和应用程序是并发执行的，当Mixed GC 的回收速度赶不上应用程序申请内存的速度时，就会引发Full GC
    * Full GC 会长时间的停顿STW


### 理解GC 日志
```
[GC (System.gc()) [PSYoungGen: 17981K->2982K(76288K)] 17981K->2990K(251392K), 0.0020654 secs] [Times: user=0.01 sys=0.01, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 2982K->0K(76288K)] [ParOldGen: 8K->2756K(175104K)] 2990K->2756K(251392K), [Metaspace: 3141K->3141K(1056768K)], 0.0067247 secs] [Times: user=0.03 sys=0.00, real=0.01 secs]

[GC [Full GC 说明垃圾收集的停顿类型，如果有Full，表示发生了Stop The World
[PSYoungGen [ParOldGen 表示GC 发生的区域，这里的区域名与GC 收集器密切相关
方括号内的 17981K->2982K(76288K) 表示"GC前该内存区域已使用容量 -> GC后该区域已使用容量(该内存区域总容量)"
方括号外的 17981K->2990K(251392K) 表示"GC前Java 堆已使用容量 -> GC后Java 堆已使用容量(Java 堆总容量)"
再往后的 0.0020654 secs 表示该内存区域GC 所占用的时间，单位是秒
[Times: user=0.01 sys=0.01, real=0.00 secs] 更具体的时间，user用户态消耗的CPU时间，sys内核态消耗的CPU时间，real操作从开始到结束所经过的墙钟时间Wall Clock Time

CPU时间和墙钟时间的区别：墙钟时间包括各种非运算的等待耗时，例如等待磁盘IO、等待线程阻塞，而CPU时间不包括这些
```

### 垃圾收集器参数总结

* UseSerialGC  虚拟机运行在Client 模式下的默认值，打开此开关后，使用 Serial + Serial Old 收集器组合进行内存回收
* UseParNewGC  打开此开关后，使用 ParNew + Serial Old 收集器组合
* UseConcMarkSweepGC  打开此开关后，使用 ParNew + CMS + Serial Old 收集器组合，Serial Old 作为CMS 出现Concurrent Mode Failure 失败后的后备收集器使用
* UseParallelGC  虚拟机运行在server 模式下的默认值，使用 Parallel Scavenge + Serial Old 收集器组合
* UseParallelOldGC  使用 Parallel Scavenge + Parallel Old 收集器组合
* SurvivorRatio  新生代中Eden 区域与Survivor 区域的容量比值，默认为8，代表 Eden : Survivor = 8:1
* PretenureSizeThreshold  直接晋升到老年代的对象大小，设置这个参数后，大于这个参数的对象将直接在老年代分配
* MaxTenuringThreshold  晋升到老年代的对象年龄，每个对象在坚持过一次Minor GC 之后，年龄就增加1，当超过这个参数值时，就进入老年代
* UseAdaptiveSizePolicy  动态调整Java 堆中各个区域的大小以及进入老年代的年龄
* HandlePromotionFailure  是否允许分配担保失败，即老年代的剩余空间不足以应付新生代整个Eden 和Survivor 区所有对象都存活的极端情况
* ParallelGCThreads  设置并行GC 时进行内存回收的线程数
* GCTimeRatio  GC 时间占总时间的比率，默认99，表示允许1%的GC 时间，仅在Parallel Scavenge 收集器时生效
* MaxGCPauseMillis  设置GC 的最大停顿时间，仅在Parallel Scavenge 收集器时生效
* CMSInitiatingOccupancyFraction  设置CMS 收集器在老年代空间被使用多少后触发垃圾回收，默认68%，仅在CMS 时生效
* UseCMSCompactAtFullCollection  设置CMS 收集器在完成垃圾回收后是否要进行一次内存碎片整理，仅在CMS 时生效
* CMSFullGCsBeforeCompaction  设置CMS 收集器在进行若干次垃圾回收后再启动一次内存碎片整理，仅在CMS 时生效


### 内存分配及回收策略























