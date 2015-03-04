# Salut

Salut is a Gtalk client with the video chat feature.

## How to Run

For now, you need to use java 7 to run Salut directly. Salut can be run on OS X, Windows and Linux. :)

For Example, on Windows 7, let's to say that you extracted Salut in `D:\Salut\`. You should see files like this:

    LICENSE                  apache-license-2.0.txt   gov
    META-INF                 ch                       images
    OSGI-OPT                 com                      javax
    TIMESTAMP                de                       native
    XPP3_1.1.4c_MIN_VERSION  gnu                      net
    about.html               gnu-public-license-2.txt org
    
The easiest way to run Salut is to create a "Shortcut". The `Target` of the Shortcut should be:

    javaw.exe -classpath "D:\Salut" -Djava.library.path=native/windows-64 com.starbugs.salut.Salut
    
In this case, `-classpath` indicates the directory of Salut, while `-Djava.library.path` tells java where to find the native codes. So the value of `-Djava.library.path` is determined by your Operating System. Here is the possible value:

    -Djava.library.path=native/linux
    -Djava.library.path=native/linux-64
    -Djava.library.path=native/mac
    -Djava.library.path=native/windows
    -Djava.library.path=native/windows-64
    
So, it is very similar to run Salut in other systems. On Mac, the command should be like this:

    java -classpath "/Users/yourusername/Applications/Salut" -Djava.library.path=native/mac com.starbugs.salut.Salut

- - -

Salut 是一款支持视频聊天的 Gtalk 客户端，旨在研究学习开源项目 Jitsi 的视频采集、传输的方法。「Salut」是法语中朋友间打招呼的话语，希望本程序能成为朋友间联络的一个工具。

## 运行方式

由于个人能力有限，目前程序只能以 Java 可执行文件的方式运行，要求 Java 7，可在 OS X、Windows 及 Linux 下使用。可执行文件打包在 `Salut-1.0.zip`。

以 64 位 Windows 7 为例，假设 `Salut-1.0.zip` 解压至 `D:\Salut\` 文件夹，在 `D:\Salut\` 下的文件夹与文件列表应与以下列表类似：

    LICENSE                  apache-license-2.0.txt   gov
    META-INF                 ch                       images
    OSGI-OPT                 com                      javax
    TIMESTAMP                de                       native
    XPP3_1.1.4c_MIN_VERSION  gnu                      net
    about.html               gnu-public-license-2.txt org
    
这时，最方便的运行 Salut 的方式是建立一个快捷方式，其中快捷方式运行的 `目标` 应为：

    javaw.exe -classpath "D:\Salut" -Djava.library.path=native/windows-64 com.starbugs.salut.Salut
    
其中，`-classpath` 参数指明 Salut 所在文件夹；`-Djava.library.path` 参数指定 native 类库的位置。`-Djava.library.path` 的取值应根据操作系统来选取：

    -Djava.library.path=native/linux
    -Djava.library.path=native/linux-64
    -Djava.library.path=native/mac
    -Djava.library.path=native/windows
    -Djava.library.path=native/windows-64
    
在理解两个参数的意义后，便能类推在其他环境中运行 Salut 的方法。如在 Mac 中通过终端运行 Salut 的可能的命令为：

    java -classpath "/Users/yourusername/Applications/Salut" -Djava.library.path=native/mac com.starbugs.salut.Salut
