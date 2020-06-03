# RoundViewGroup简介
RoundConstraintLayout是一个继承自ConstraintLayout并实现统一圆角功能的ViewGroup，因此不管子View什么样子都能统一实现圆角。

## 如何使用
```
repositories {
	...
	maven { url 'https://jitpack.io' }
}
```
```
dependencies {
	implementation 'com.github.Bigoy:RoundViewGroup:1.0.0'
}
```

## 效果预览
![preview](https://github.com/Bigoy/RoundViewGroup/blob/master/%E5%9C%86%E8%A7%92%E5%B8%83%E5%B1%80%E9%A2%84%E8%A7%88.png)

## 支持设置的效果
设置ViewGroup四个角的独立圆角大小

设置内边框：大小、颜色

相比clipPath()方式，本布局支持抗锯齿

## 使用注意
不要调用设置背景的任何方法，且设置的背景不会生效
