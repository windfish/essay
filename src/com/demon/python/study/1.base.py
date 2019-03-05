#!/usr/bin/env python3
# 设定脚本执行的python 环境
# -*- coding: utf-8 -*-
# 告诉python 解释器，按照UTF-8 编码读取源代码

print('Hello world!')
print('The quick brown fox', 'jumps over', 'the lazy day')
print(100+200)
print(1024 * 768)

#name=input('please input your name: ')
#print('hello, ', name)

print('\n----------字符类型与变量------------')

# 转义字符 \
print('\\asd\'adad')

# 用 r'' 表示 '' 内部的字符串不转义
print(r'\\asd\'adad')

# 用 '''内容''' 表示多行内容
print('''第一行
第二行
第三行''')
# 还可以加 r 使用，r'''内容''' 表示内部字符串不转义
print(r'''first\n
second''')

# 除法运算： / 除法，计算结果为浮点数；// 地板除，两个整数的除法仍是整数
print(10/3)
print(10//3)
print(8.0//2)

# ord() 获取字符的整数编码表示  chr() 把编码转换为字符
print(ord('A'))
print(ord('中'))
print(chr(66))
print(chr(20013))

# bytes 类型的数据用 b前缀的单引号或双引号 表示
x = b'abc'
print(x)

print('\n----------字符编码------------')

# 以Unicode 表示的str 可以使用encode() 编码为指定的bytes；bytes 使用decode() 变为str，可以传入 errors='ignore' 忽略错误的字节
print('ABC'.encode('ascii'))
print('中文'.encode('utf-8'))
# print('中文'.encode('ascii'))  # 中文不能使用acsii 编码

print(b'ABC'.decode())
print(b'\xe4\xb8\xad\xe6\x96\x87'.decode('utf-8'))
print(b'\xe4\xb8\xad\xe6\x96\x87\xff'.decode('utf-8', errors='ignore')) # \xff 是错误的字节

print('\n----------字符串格式化------------')

# 字符串格式化，和C一致，用 % 实现
# %d 数字，%s 字符串，%f 浮点数，%x 十六进制整数，若不确定类型，%s 永远起作用；若% 为一个字符，则使用% 进行转义，即%% 表示%
print('hello, %s' %'阿萨德')
print('Hi, %s, you have $%d' %('xxx', 1000000))
print('Completion ratio: %d %%' %16)

# 还可以使用 format() 格式化字符串，占位符为 {0},{1}... 
print('{0}的成绩提升了{1:.1f}%'.format('xxx',17.123))
print('%s的成绩提升了%.1f%%' %('xxx',17.123))

print('\n----------列表list------------')
# list列表：len()函数获取列表的元素个数
List = ['switch', 'ps4', 'xbox']
print(List)
print(len(List))

# 用索引来访问列表数据，正向索引从0开始，逆向索引从-1开始
print(List[1])
print(List[-1])

# list 是可变列表，append(value) 追加元素到末尾，insert(index,value) 插入指定位置
List.append('3ds')
print(List)
List.insert(3, 'xbox one')
print(List)

# pop() 删除末尾的元素，pop(index) 删除指定位置的元素
List.pop()
print(List)
List.pop(3)
print(List)

# list 元素的数据类型可以不一样，还可以是另一个list
List.append(123)
innerList = ['java', 'c++', 'javaScript']
List.append(innerList)
print(List)

print('\n----------有序列表tuple------------')
# tuple有序列表：一旦初始化就不能修改，获取元素方法与list一样
Tuple = ('java', 'c++', 'php')
print(Tuple)
print(Tuple[0])

# 定义只有一个元素的tuple 时，需要加一个逗号来消除与运算符号的歧义
tupleOne = (1,)
print(tupleOne)
tupleEmpty = ()
print(tupleEmpty)

# tuple 的不变，指的是其每个元素，指向永远不变，若元素指向list，则其list内部的元素还是可以变

print('\n----------添加判断if------------')
# if <条件1>:
#   <执行1>
# elif <条件2>:
#   <执行2>
# else:
#   <执行3>
# 根据缩进规则，判断if 后执行几句语句；判断条件可以使用if x>20，也可以使用if x，只要x 是非零数值、非空字符串、非空list等，就判断为true，否则为false
x = 3
if x:
  print('if逻辑')
else:
  print('else')

height = 1.75
weight = 80.5
BMI = weight / (height * height)
if BMI < 18.5:
  print('过轻')
elif BMI < 25:
  print('正常')
elif BMI < 28:
  print('过重')
elif BMI < 32:
  print('肥胖')
else:
  print('严重肥胖')

print('\n----------循环------------')
# for 循环：for x in list or tuple
# while 循环：while <条件>:
for x in List:
  print(x)

# range(max) 函数，可以生成一个整数序列，序列不包含参数值；list()函数可以转换为list
print(list(range(10)))

print('\n----------字典dict------------')
# dict 字典，其他语言也称map，使用键值对(key-value)存储，dict 的key必须为不可变对象，其实就是利用hash算法计算key所在位置
d = {'switch': 2200, 'ps4 pro': 3000, 'xbox one x': 3600}
print(d['switch'])

# 若key不存在，则dict 会报错，可使用 <key> in <dict> 检查；还可以使用get()函数，若key不存在，则返回None，或自己指定返回值
print('switch' in d)
print(d.get('aaa'))
print(d.get('aaa', 'key not exists'))

print('\n----------set------------')
# set，只存储key，且不可重复的dict，要构造一个set，需要传入一个list作为输入参数
print(set([1,2,3]))
print(set([3,2,1,1,2,3,3]))

# 使用add(key) 添加元素到set，使用remove(key) 删除元素
s = (set([1,2,3]))
s.add('asd')
print(s)
s.remove('asd')
print(s)

# 不能将不可变对象(例如list)放入set 中，因为不可变对象无法判断两个对象是否相等，也就不能保证set 不能有重复对象
#s.add([4,5])
#print(s)










