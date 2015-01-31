# performance

performance包括两种启动方式：嵌入式启动和容器启动
### 1、嵌入式启动
 所谓嵌入式启动，即在通过api启动，如果你使用maven，可以在pom中加入如下依赖获取performance
```Xml
<dependency>
    <groupId>com.github.jobop</groupId>
    <artifactId>performance</artifactId>
    <version>1.0.0</version>
</dependency>
```
然后通过如下代码即可测试你的业务
```Java
public class PerformanceTest implements PerformanceBizSpi {
	public static void main(String[] args) {
		new PerformanceTask().t(1).c(1l).l("test.log").addTest(new PerformanceTest()).start();
	}

	@Override
	public boolean execute() {
		System.out.println("模拟50%成功率");
		return (new Random().nextInt(10) % 2) == 0;

	}
}

```
    其中

    t:持续时间，分为单位
    c:启动进程数
    l:报告输出路径
    n:被测试的类全路径，performance会根据这个全路径过滤后面addTest的用例，不指定则addTest的所用用例均会被测试
    addTest:需要增加到测试列表的用例，需要实现PerformanceBizSpi接口。
    start:开始测试


在测试完毕后，会在l指定的文件中输出报告如下：

    接口 com.cmf.ec.trade.webapp.test.cases.ErrorCodeSpi 第1次输出,距离上次时间间隔为:60000ms
    现在时间为:1422617565261
    本间隔执行次数为:755.00
    总执行次数为:755.00
    最近一分钟平均耗时为:79.00ms
    最近一分钟平均tps为:12.58
    最近一分钟成功率为:100.00%
    总平均耗时为:79.00ms
    总平均tps为:12.58
    总成功率为:100.00%
    *******************************************************************************


### 2、容器式启动
	有时为了可以在性能比较高的机器中测试，我们需要使用容器方式启动。
		1、下载以下zip包，并解压。[performance-1.0.0.zip](https://github.com/jobop/release/blob/master/performance/performance-1.0.0.zip?raw=true)
		2、你要设置一个app目录，修改conf/config.properties中的LIB_BASE_PATH即可
		3、你需要在app目录中新建子文件夹，然后把你的用例所依赖的jar包全部放到里面（用例同样需要实现PerformanceBizSpi接口）。
		4、在你的子文件夹中创建spi.lst文件，文件内容即此子文件夹中所包含全部用例的全路径，每个一行，如下所示：
   com.cmf.ec.testsub.TestTradeErrorCodeSpi
   com.cmf.ec.testsub.TestSample    
		5、执行bin目录中的脚本，脚本参数与api命名相同。
