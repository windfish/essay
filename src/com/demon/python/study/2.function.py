#!/usr/bin/env python3
# -*- coding: utf-8 -*-

# python 内置了很多函数，可以直接调用，具体查看：https://docs.python.org/3/library/functions.html
print(str(hex(1000)))

print("\n---------定义函数----------")
# 定义函数使用 def 语句，def 函数名(参数...):，然后在缩进块中编写函数体，返回值用return 语句返回，若没有return 语句，默认return None
# isinstance 进行数据类型检查
def myAbs(x):
  if not isinstance(x, (int, float)):
    raise TypeError('bad operand type')
  if x >= 0:
    return x
  else:
    return -x

print(myAbs(-80))
print(myAbs(90))

# 若将函数保存在另一个py文件中(testfunction.py)，则要使用需要用import 导入函数
from testfunction import my_abs
print(myAbs(-20))

# pass 语句可以用来作为占位符，什么都不做
def nop():
  pass

age = 18
if age >= 18:
  pass

# 函数支持返回多个值，返回值时一个tuple，多个变量可以接收一个tuple，会按顺序赋值给变量
import math

# 从一个点移动到另一个点，给出坐标、位移和角度，返回新坐标
def move(x, y, step, angle=0):
  nx = x + step * math.cos(angle)
  ny = y + step * math.sin(angle)
  return nx, ny

x, y = move(100, 100, 60, math.pi / 6)
print(x, y)
print(str(x) + "  ------  " + str(y))
r = move(100, 100, 60, math.pi / 6)
print(r)

# 练习，定义函数，接收a、b、c 三个参数，返回 ax^2 + bx + c = 0 的两个解
# 一元二次方程 ax^2 + bx + c = 0，根的判别式 △=b^2-4ac，>0时，有两个不相等的实数根，=0时，有两个相等的实数根，<0时，没有实数根
# 求根公式：x1 = -b + sqrt(b^2-4ac)/2a  x2 = -b - sqrt(b^2-4ac)/2a
class UnsolvableError(Exception):
  def __init__(self, message):
      self.message = message

def quadratic(a, b, c):
  if a == 0:
    return -c/b
  elif b == 0:
    return math.sqrt(-c/a)
  elif b*b-4*a*c < 0:
    #raise UnsolvableError('无实数解')
    return '无实数解'
  else:
    return (-b + math.sqrt(b*b-4*a*c)/2*a), (-b - math.sqrt(b*b-4*a*c)/2*a)

print(quadratic(2,3,1))
print(quadratic(2,1,1))

print("\n---------函数的默认参数----------")
# 函数的参数支持默认值，要注意几个原则：
# 1、必选参数在前，默认参数在后，否则解释器会报错
# 2、默认参数设置方法，当有多个参数时，把变化大的参数放前面，变化小的参数放后面，变化小的参数可以作为默认参数
# 3、调用时，必选参数按顺序传入，默认参数可以顺序提供，也可以不按顺序提供，不按顺序时，需要把参数名写上
def enroll(name, gender, age=6, city='Hangzhou'):
  print('name:', name)
  print('gender', gender)
  print('age', age)
  print('city', city)
  print('')

enroll('one', 1)
enroll('two', 1, 7)
enroll('three', 1, city='Beijing')

# 默认参数必须指向不变对象
# 若指向可变对象，例如list，则使用默认参数调用时，会造成默认参数值不是预期的值

def add_end(L=[]):
  L.append('END')
  print(L)
  print('')

add_end()
add_end()    # 默认参数，不是[]，而是第一次调用后的['END']

# add_end 的默认参数，修改为None
def add_end_fix(L=None):
  if L is None:
    L = []
  L.append("End")
  print(L)
  print('')

add_end_fix()
add_end_fix()

print("\n---------函数的可变参数----------")
# 函数支持可变参数，可变参数为参数前加 *，函数内部，接收的是一个tuple；调用时，直接传0或多个参数，若要传list或tuple，可以在list或tuple 前加 * 传入
def sum(*nums):
  sum = 0
  for n in nums:
    sum = sum + n
  return sum

print(sum(1, 2))
L = [1, 2, 3]
print(sum(*L))
print()

print("\n---------函数的关键字参数----------")
# 关键字参数，在参数前加 **，函数内部会组装成一个dict，允许传入0或多个含参数名的参数
# 若要传入一个dict，可以在dict 前加 ** 传入
def person(name, age, **kw):
  print('name:', name, 'age:', age, 'other:', kw)

person('aaa', 22)
person('bbb', 20, city='Beijing')
person('ccc', 55, city='Beijing', gender='M')

D = {'city': 'Beijing', 'zipcode':123456}
person('ddd', 30, **D)

print("\n---------函数的命名关键字参数----------")
# 命名关键字参数，可以限制传入的关键字参数名字，需要使用一个特殊分隔符 *，* 后面的参数被视为关键字参数；必须传入参数名，否则报错
def person(name, age, *, city, job):
  print(name, age, city, job)

person('jack', 24, city='Beijing', job='Engineer')

# 如果函数已经有一个可变参数了，那么关键字参数前就不需要 * 了；关键字参数还可以添加默认值
def person(name, age, *args, city='Shanghai', job):
  print(name, age, args, city, job)

person('jack', 24, 'Beijing', city='Beijing', job='Engineer')
person('make', 26, '12313', job='Engineer')

print("\n---------函数的参数组合----------")
# 函数可以组合使用必选参数、默认参数、可变参数、关键字参数和命名关键字参数，参数定义的顺序必须是：必选参数、默认参数、可变参数、命名关键字参数、关键字参数


# 一个或多个数计算乘积
def product(first=None, *nums):
  if first is None:
    raise TypeError('no params')
  result = first
  for x in nums:
    result = result * x
  return result

print(product(5))
print(product(5, 6))
print(product(5, 6, 7))
try:
  print(product())
  print('error')
except TypeError:
  print('catch error')

print("\n---------递归函数----------")
# 使用递归函数要防止堆栈溢出
def fact_overflow(n):
  if n==1:
    return 1
  return n * fact_overflow(n-1)

print(fact_overflow(5))
#print(fact_overflow(1000))

# 解决递归堆栈溢出的方法是通过“尾递归”优化，尾递归是指：在函数返回的时候，调用自身本身，并且return 语句不能包含表达式。这样，解释器把尾递归优化，使递归无论调用多少次，都只占用一个栈帧，不会出现栈溢出的情况。
# 但是python 解释器并没有针对尾递归做优化
def fact(n):
  return fact_iter(n, 1)

# n 待计算的数字  product 上一步的计算结果
# 每次递返回的是函数本身，n-1 和 n*product 在函数调用之前就会计算，不影响函数调用
def fact_iter(n, product):
  if n == 1:
    return product
  return fact_iter(n-1, n * product)
    
#print(fact(1000))

print("\n---------汉诺塔----------")
# 汉诺塔
# 简单分为三个步骤：1.把n-1个盘子由A移到到B，2.把第n个盘子由A移动到C，3.把n-1个盘子由B移动到C
def moveTowerOfHanoi(n, a, b, c):
  if n == 1:
    print(a, ' --> ' ,c)
  else:
    moveTowerOfHanoi(n-1, a, c, b)
    moveTowerOfHanoi(1, a, b, c)
    moveTowerOfHanoi(n-1, b, a, c)

moveTowerOfHanoi(3, 'A柱', 'B柱', 'C柱')















