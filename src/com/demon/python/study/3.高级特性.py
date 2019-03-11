#!/usr/bin/env python3
# -*- coding: utf-8 -*-

print('-----------切片 Slice-------------')
L = ['python', 'java', 'c++', 'php', 'js']
print(L[0:3])    # 从索引0开始，到索引3为止，不包括索引3。如果第一个索引是0，还可以省略L[:3]

L = list(range(100))
print(L[:10])    # 前10个数
print(L[-10:])   # 后10个数
print(L[10:20])  # 前11-20个数
print(L[:10:2])  # 前10个数，每2个取一个
print(L[::5])    # 所有的数，每5个取一个

# 字符串也可以进行切片操作
str_ = 'ABCDEFG';
print(str_[:3])
print(str_[::2])

# 利用切片，实现trim 函数
def strTrim(s):
  if not isinstance(s, str):
    raise TypeError('bad request param, must is str')
  while(True):
    if s[:1] == ' ':
      s = s[1:]
    elif s[-1:] == ' ':
      s = s[:-1]
    else:
      break
  return s;
    
print(strTrim('  12345  '))
print(strTrim('   Hello world'))
print(strTrim('abcd   '))
print(strTrim(''))


print('\n-----------迭代-------------')
# 迭代是通过for循环完成的，不仅可以迭代list或tuple，也可以迭代字符串和dict
print('迭代字符串')
for s in 'abc':    # 迭代字符串
  print(s)

print('\n迭代dict')
d = {'a':1, 'b':2, 'c':3}
for key in d:  # 迭代dict，默认只迭代key
  print(key)
for value in d.values():   # 迭代value
  print(value)
for k, v in d.items():   # 同时迭代key 和value
  print(k, v)

# 判断一个对象是否是可迭代对象，通过collections 模块的Iterable 类型判断
# collections 3.8就不支持了，新的模块为 collections.abc
from collections.abc import Iterable
print(isinstance('abc', Iterable))
print(isinstance([1,2,3], Iterable))
print(isinstance(123, Iterable))

# 迭代中获取list元素的下标，使用 enumerate 函数把一个list变成 索引-元素对
for index, value in enumerate(['A', 'B', 'C']):
  print(index, value)

# 使用迭代查找list的最小和最大值，并返回一个tuple
def findMinAndMax(L=None):
  if L == None or len(L) == 0:
    return (None, None)
  min = None;
  max = None;
  for l in L:
    if min == None:
      min = l
    elif min > l:
      min = l
    if max == None:
      max = l
    elif max < l:
      max = l
  return (min, max)

# 测试
if findMinAndMax([]) != (None, None):
    print('测试失败!')
elif findMinAndMax([7]) != (7, 7):
    print('测试失败!')
elif findMinAndMax([7, 1]) != (1, 7):
    print('测试失败!')
elif findMinAndMax([7, 1, 3, 9, 5]) != (1, 9):
    print('测试失败!')
else:
    print('测试成功!')

print('\n-----------列表生成式-------------')
# range(1, 11) 生成列表[1, 2, 3 ... 10]
print(list(range(1, 11)))

# [1*1, 2*2, ... 10*10]的列表
print([x * x for x in range(1, 11)])

# for 循环后还可以加上if 判断，例如生成偶数的平方
print([x*x for x in range(1, 11) if x%2 == 0])

# 还可以使用两层循环，生成全排列
print([m + n for m in 'ABC' for n in 'XYZ'])

# 还可以使用两个变量
d = {'x':'A', 'y':'B', 'z':'C'}
print([k+'='+v for k, v in d.items()])

# 将list中的字符串变为小写
L = ['Hello', 'World', 'IBM', 'Apple']
print([s.lower() for s in L])

# 通过if，去除列表里的非字符串数据
L1 = ['Hello', 123, 'World', 'IBM', 'Apple', 234]
print([s.lower() for s in L1 if isinstance(s, str)])

print('\n-----------生成器-------------')
# 通过列表生成式，可以直接创建一个列表，但是受内存限制，列表的大小是有限的。如果列表元素可以按照某种算法推算出来，那么python可以不必创建完整的list，从而节省内存空间
# 这种一边循环一边计算的机制，称为生成器：generator

# 第一种创建方法：把一个列表生成式的[]改为()
L = [x*x for x in range(10)]
print(L)

g = (x*x for x in range(10))
print(g)

# 通过next()函数打印下一个返回值，generator保存的是算法，每次调用next，就计算下一个元素的值，若没有更多元素时，会抛出StopIteration错误。还可以使用for 循环迭代
print(next(g))
for n in g:
  print(n)

# 第二种创建方法，通过函数创建。如果一个函数定义中包含yield 关键字，那么这个函数就是一个generator
# generator 和函数的执行流程不一样。函数是顺序执行，遇到return 语句或最后一行语句就返回；generator 在每次调用next()时执行，遇到yield 语句返回，再次执行时从上次返回的yield 语句出继续执行
def odd():
  print('step 1')
  yield 1
  print('step 2')
  yield 2

o = odd()
print(next(o))
print(next(o))
#print(next(o))  # 执行第三次时，已没有yield 可执行了，就报错

# 斐波那契数列
def fib(max):
  n, a, b = 0, 0, 1
  while n < max:
    yield b
    a, b = b, a+b
    n = n + 1
  return 'end'

f = fib(6)
for n in f:
  print(n)

# for循环调用generator 时，拿不到return语句的值，如果想拿到return 的值，需要捕获StopIteration错误，返回值包含在StopIteration的value中
ff = fib(6)
while True:
  try:
    x = next(ff)
    print('g: ', x)
  except StopIteration as e:
    print('generator return value: ', e.value)
    break

# 杨辉三角
def triangles(max):
  L = []
  preL = []
  n = 1
  while n <= max:
    preL = L
    L = []
    i = 0
    while i<n:
      if i==0 or i == n-1:
        L.append(1)
      else:
        L.append(preL[i-1] + preL[i])
      i = i + 1
    n = n + 1
    yield L

for t in triangles(10):
  print(t)
  
print('\n-----------迭代器-------------')
# 可迭代对象 Iterable：可以直接作用于for 循环的对象，包括集合数据类型，例如：list、tuple、dict、set、str；也包括generator，包括生成器和带yield 的generator function
# 迭代器对象 Iterator：可以直接作用于for 循环，还可以被next()函数不断调用并返回下一个值
# 判断Iterable 对象，isinstance(x, Iterable)；判断Iterator 对象，isinstance(x, Iterator)；需从 collections.abc 模块中导入相应的对象
from collections.abc import Iterable
print(isinstance([], Iterable))
print(isinstance((), Iterable))
print(isinstance('abc', Iterable))
print(isinstance((x for x in range(10)), Iterable))
print(isinstance(100, Iterable))

from collections.abc import Iterator
print(isinstance((x for x in range(10)), Iterator))
print(isinstance([], Iterator))
print(isinstance('abc', Iterator))

# 集合类数据类型，可以通过 iter() 函数转变为Iterator
print(isinstance(iter([]), Iterator))
L = [1, 2, 3]
ITER = iter(L)
print(next(ITER), next(ITER), next(ITER))

# Iterator 表示的是一个数据流，可以被next()不断调用并返回下一个数据，但是无法提前知道序列的长度，所以Iterator 是惰性的，只在需要返回数据时，才计算下一个数据是什么。
# Iterator 可以表示一个无限大的数据流，例如全体自然数，而使用集合永远不能存储全体自然数



