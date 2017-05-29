* **HTML是网页内容的载体**。内容就是网页制作者放在页面上想要让用户浏览的信息，可以包含文字、图片、视频等。


* **CSS样式是表现**。就像网页的外衣。比如，标题字体、颜色变化，或为标题加入背景图片、边框等。所有这些用来改变内容外观的东西称之为表现。


* **JavaScript是用来实现网页上的特效效果**。如：鼠标滑过弹出下拉菜单。或鼠标滑过表格的背景颜色改变。还有焦点新闻（新闻图片）的轮换。可以这么理解，有动画的，有交互的一般都是用JavaScript来实现的。


```html
<!DOCTYPE HTML>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title>Html和CSS的关系</title>
        <style type="text/css">
        h1{
            font-size:12px;
            color:#930;
            text-align:center;
        }
        </style>
    </head>
    <body>
        <h1>Hello World!</h1>
    </body>
</html>
```

认识html文件基本结构
----------------------
一个HTML文件是有自己的结构的固定的结构的
```
<html>
	<head></head>
	<body></body>
</html>
```
1. <html></html>称为根标签，所有的网页标签都在<html></html>中。
2. <head> 标签用于定义文档的头部，它是所有头部元素的容器。头部元素有<title>、<script>、 <style>、<link>、 <meta>等标签，头部标签在下一小节中会有详细介绍。
3. 在<body>和</body>标签之间的内容是网页的主要内容，如<h1>、<p>、<a>、<img>等网页内容标签，在这里的标签中的内容会在浏览器中显示出来。

认识div在排版中的作用
--------------
在网页制作过程过中，可以把一些独立的逻辑部分划分出来，放在一个<div>标签中，这个<div>标签的作用就相当于一个容器。在网页制作过程过中，可以把一些独立的逻辑部分划分出来，放在一个<div>标签中，这个<div>标签的作用就相当于一个容器。

1. <table>…</table>：整个表格以<table>标记开始、</table>标记结束。
2. <tbody>…</tbody>：如果不加<thead><tbody><tfooter> , table表格加载完后才显示。加上这些表格结构， tbody包含行的内容下载完优先显示，不必等待表格结束后在显示，同时如果表格很长，用tbody分段，可以一部分一部分地显示。（通俗理解table 可以按结构一块块的显示，不在等整个表格加载完后显示。）
3. <tr>…</tr>：表格的一行，所以有几对tr 表格就有几行。
4. <td>…</td>：表格的一个单元格，一行中包含几对<td>...</td>，说明一行中就有几列。
5. <th>…</th>：表格的头部的一个单元格，表格表头。
6. 表格中列的个数，取决于一行中数据单元格的个数。

```html
<!DOCTYPE HTML>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>为表格添加边框</title>
<style type="text/css">
table tr td,th{border:1px solid #000;}
</style>
</head>

<body>
<table summary="">
  <tr>
    <th>班级</th>
    <th>学生数</th>
    <th>平均成绩</th>
  </tr>
  <tr>
    <td>一班</td>
    <td>30</td>
    <td>89</td>
  </tr>
  <tr>
    <td>二班</td>
    <td>35</td>
    <td>85</td>
  </tr>
  <tr>
    <td>三班</td>
    <td>32</td>
    <td>80</td>
  </tr>
</table>

</body>
</html>
```

