# python3.7 安装遇到的问题
### windows
* 运行缺少 api-ms-win-crt-runtime-l1-1-0.dll，安装 KB2999226补丁程序，win7 64位版本：https://www.microsoft.com/en-us/download/details.aspx?id=49093

### CentOS 编译安装
* zipimport.ZipImportError: can’t decompress data
缺少zlib 的相关工具包，yum -y install zlib*，然后 make && make install
* ModuleNotFoundError: No module named '_ctypes'
缺少libffi-devel 依赖包，yum -y install libffi-devel，然后 ./configure && make && make install


# 字符编码
计算机只能处理数字，若要处理文本，需要将文本转换为数字才能处理。
计算机中采用8个比特bit 作为一个字节byte，一个字节能表示的最大的数为255（二进制11111111=十进制255），若要表示更大的数字，需要更多的字节。

* ASCII 编码：包含大小写字母、数字和一些符号。
* 其他文字的编码：GB2313 中文，Shift_JIS 日文等，但多文本时，就会产生乱码。
* Unicode 编码：把所有语言统一到一套编码里，这样就不会产生乱码了。常用的是用两个字节表示一个字符（非常偏僻的字符，就需要4个字节）
* ASCII 转 Unicode：ASCII 编码时一个字节，Unicode 编码是两个字节，转换时，只需要在ASCII 编码前补0就可以了。当文本是英文的话，用Unicode 编码就会多出一倍的存储空间。
* UTF-8 编码：把一个Unicode 字符根据不同的数字大小编码成1-6个字节，常用的英文被编译为1个字节，汉字通常是3个字节，很生僻的字符才会编码为6个字符。使用UTF-8 编码，就会节省空间


