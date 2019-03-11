#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# 函数式编程是一种抽象程度很高的编程范式，纯粹的函数式编程语言编写的函数没有变量，任意一个函数，只有输入是确定的，输出就是确定的，这种纯函数我们称为没有副作用。而允许使用变量的程序设计语言，由于内部的变量状态不确定，同样的输入，可能得到不同的输出，这种函数是有副作用的。
# 函数式编程的一个特点是，允许把函数本身作为函数作为参数传入另一个函数，还允许返回一个函数

print('------------高阶函数----------------')
# 变量可以指向函数
print(abs)
f=abs
print(f(-10))

# 一个函数可以接收另一个函数作为参数，这种函数就称为高阶函数
def add(x, y, f):
  return f(x) + f(y)

print(add(-4, 6, abs))

print('\n-----------map/reduce--------------')
# map() 函数接收两个参数，一个是函数，一个是Iterable，map将传入的函数依次作用到序列的每个元素，并把结果作为新的Iterator 返回
def f(x):
  return x * x

r = map(f, [1, 2, 3, 4, 5, 6, 7, 8, 9])
print(r)          # 结果是一个Iterator
print(list(r))

# reduce() 函数接收两个参数，一个是函数，函数必须接收两个参数，一个是Iterable，reduce把结果继续和序列下一个元素做累积计算
from functools import reduce
def add(x, y):
  return x + y

print(reduce(add, [1, 2, 3 ,4 ,5]))

# 将[1, 3, 5, 7, 9]转为13579
def fn(x, y):
  return x * 10 + y

print(reduce(fn, [1, 3, 5, 7, 9]))

# 字符串转int
def str2int(s):
  DIGITS = {'0':0, '1':1, '2':2, '3':3, '4':4, '5':5, '6':6, '7':7, '8':8, '9':9}
  def fn(x, y):
    return x * 10 + y
  def char2num(c):
    return DIGITS[c]

  return reduce(fn, map(char2num, s))

print(str2int('123456'))

# 格式化单词，首字母大写，其余小写
def formatWord(word):
  return word[0:1].upper() + word[1:].lower()
L1 = ['adam', 'LISa', 'barT']
print(list(map(formatWord, L1)))

# list中各数字的积
def prod(L):
  def fn(x, y):
    return x * y
  return reduce(fn, L)

print(prod([3, 5, 7, 9]))

# 字符串转浮点数
def str2float(s):
  def f1(x, y):
    return int(x) * 10 + int(y)
  def f2(x, y):
    return float(x) * 0.1 + int(y)
  
  s = s.split('.')
  print(s[0], s[1])
  print(s[1][::-1], reduce(f2, s[1][::-1]))
  if len(s) == 2:
    return reduce(f1, s[0]) + 0.1 * reduce(f2, s[1][::-1])
  else:
    return reduce(f1, s[0])

print(str2float('123.456'))

print('\n-----------filter--------------')
# filter() 接收一个函数和一个序列，将传入的函数作用于序列的每一个元素，然后根据函数返回值时True或False决定保留还是丢弃该元素，filter返回的也是一个Iterator
def is_odd(n):
  return n % 2 ==1

print(list(filter(is_odd, [1,2,3,4,5,6,7,8,9,10,15])))

# 求素数，埃氏筛法：1、列出从2开始的所有自然数 2、取序列的第一个数2，它一定是素数，然后把序列中2的倍数筛掉 3、取新序列第一个数3，将3的倍数筛掉 4、不断筛选下去，就可以得到所有的素数了
def primes():
  def _odd_iter():
    n = 1
    while True:
      n = n + 2
      yield n

  def _not_divisible(n):
    return lambda x: x % n > 0

  yield 2
  it = _odd_iter()
  while True:
    n = next(it)
    yield n
    it = filter(_not_divisible(n), it)

for n in primes():
  if n < 100:
    print(n)
  else:
    break

# 求回数
def is_palindrome(n):
  s = str(n)
  if s[::] == s[::-1]:
    return True
  else:
    return False

print(list(filter(is_palindrome, range(1, 1000))))

print('\n------------sorted 排序----------------')
# sorted() 函数，可对list进行排序；也可以接受一个key 函数来实现自定义排序，key指定的函数会作用于list的每一个元素上，并根据结果进行排序
# 要进行反向排序，可以传入第三个参数 reverse=True
L = [36, 5, -12, 9, -21]
print(sorted(L))
print(sorted(L, key=abs))

strL = ['bob', 'about', 'Zoo', 'Credit']
print(sorted(strL))
print(sorted(strL, key=str.lower))
print(sorted(strL, key=str.lower, reverse=True))

# list中包含一组tuple表示学生的名字和成绩，用名字排序
L = [('Bob', 75), ('Adam', 92), ('Bart', 66), ('Lisa', 88)]
def by_name(t):
  return t[0]
def by_score(t):
  return t[1]

L2 = sorted(L, key=by_name)
print(L2)
print(sorted(L, key=by_score, reverse=True))

print('\n------------返回函数---------------')
def lazy_sum(*args):
  def sum():
    ax = 0
    for n in args:
      ax = ax + n
    return ax
  return sum

print(lazy_sum(1, 3, 5, 7, 9))
f1 = lazy_sum(1, 3, 5, 7, 9)
f2 = lazy_sum(1, 3, 5, 7, 9)
print(f1==f2)
print(f1())

# lazy_sum() 函数返回值为函数，其定义了内部函数sum，内部函数可以引用外部函数lazy_sum的参数和局部变量，当返回函数sum时，相关参数和变量都保存在返回的函数中，称为“闭包”。
# 每次调用返回的函数，都是一个新的函数

def count():
  fs = []
  for i in range(1, 4):
    def f():
      return i*i;
    fs.append(f)
  return fs

f1, f2, f3 = count()
print(f1(), f2(), f3())

# 三个函数执行结果都是9，因为返回的函数引用了变量i，但并非立刻执行，当执行时，i的值已经变成了3
# 解决方法：再定义一个函数，用该函数的参数绑定循环变量当前的值
def count1():
  fs = []
  def f(j):
    def g():
      return j*j
    return g
  for i in range(1, 4):
    fs.append(f(i))
  return fs

f1, f2, f3 = count1()
print(f1(), f2(), f3())

# 计数器
def createCounter():
  def g():
    n = 1
    while True:
      yield n
      n = n + 1
  gc = g()
  def counter():
    return next(gc)
  return counter

counterA = createCounter()
print(counterA(), counterA(), counterA(), counterA(), counterA())

counterB = createCounter()
print(counterB(), counterB())

print('\n------------匿名函数---------------')
# lambda x: x * x
# 关键字 lambda 表示匿名函数，冒号前的x 表示函数参数。匿名函数只能有一个表达式，不需要写return，返回值就是表达式的结果
# 可以将匿名函数赋值给变量，再通过变量来调用；也可以将匿名函数作为返回值返回
f = lambda x: x*x
print(f(3))

print(list(filter(lambda n: n%2==1, range(1,20))))

print('\n------------装饰器---------------')
# 函数对象有一个__name__属性，可以拿到函数的名字
def now():
  print('2019-3-11')
f = now
print(now.__name__, f.__name__)

# 要增强now()函数的功能，但又不希望修改now()函数的定义，比如增加日志打印，这种在代码运行期间动态增加功能的方式，称为“装饰器（Decorator）”
def log1(func):
  def wrapper(*args, **kw):
    print('call %s():' % func.__name__)
    return func(*args, **kw)
  return wrapper

@log1
def now1():
  print('now1: 2019-3-11')
now1()

# 若decorator 本身需要传入参数
def log2(text):
  def decorator(func):
    def wrapper(*args, **kw):
      print('%s %s():' % (text, func.__name__))
      return func(*args, **kw)
    return wrapper
  return decorator

@log2('execute')
def now2():
  print('now2: 2019-3-11')
now2()

# now2 的函数名，并不是原始的函数名，使用functools模块的wraps，将原始函数的属性复制到wrapper()函数中
print(now2.__name__)

# 既支持无参数，也支持有参数的decorator
import functools
def log3(text='execute'):
  def decorator(func):
    @functools.wraps(func)
    def wrapper(*args, **kw):
      print('%s %s():' % (text, func.__name__))
      return func(*args, **kw)
    return wrapper
  return decorator

@log3()
def now3():
  print('now3: 2019-3-11')
@log3('custom define')
def now4():
  print('now4: 2019-3-11')
now3()
print(now3.__name__)
now4()

# 打印函数执行时间的decorator
import time, functools
def metric(fn):
  @functools.wraps(fn)
  def wrapper(*args, **kw):
    start = time.time()
    r = fn(*args, **kw)
    end = time.time()
    duration = end - start
    print('%s executed in %s ms' % (fn.__name__, duration))
    return r
  return wrapper

@metric
def fast(x, y):
  time.sleep(0.0012)
  return x + y
print(fast(11, 22))

@metric
def slow(x, y, z):
  time.sleep(0.11)
  return x * y * z
print(slow(11, 22, 33))
