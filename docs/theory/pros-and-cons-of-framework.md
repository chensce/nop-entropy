# 如何评价一种框架技术的好坏？

一个很有趣的问题是，一个新的框架技术出现的时候，我们如何评价它的好坏？NopORM引擎今年开源以来，也收到了一些反馈，不过大部分人应该没有看懂NopORM引擎的理论部分，所以普遍的疑惑是NopORM相对于其他ORM引擎到底有什么具体的优势？ 在本文中我想谈一谈评价一个框架可以有哪些不以人的经验、喜好、熟悉程度为转移的客观标准。

> 有趣的是，有些人虽然自己从来不看理论部分，但是谈到国内外软件技术的差距来，论调却是“国内开发都强调实用主义，在软件方法论方面的差距太大了”。这只能说是一种叶公好龙的迷惑行为了。

> 关于NopORM引擎的理论分析，可以参见 [低代码平台需要什么样的ORM引擎？(1)](https://zhuanlan.zhihu.com/p/543252423)和[低代码平台需要什么样的ORM引擎?(2)](https://zhuanlan.zhihu.com/p/545063021)

## 朴素的评判标准

很多人对于框架技术的评判标准是很朴素的，比如：

1. **使用XML这种被淘汰的技术作为信息载体，差评！**
   根据某种局部的技术表现形式来判断一种框架技术的优劣无疑是片面化、表面化的，一种创新性技术的本质是提供了一种新的逻辑组织机制，这种机制具体以什么样的技术形式作为表观载体是一个次要性的问题。

2. **用的人多吗？文档详细吗？**
   这是最实用主义的一种观点。但它评价的是对我这种拿来主义的人而言，这个框架是否容易使用的问题，而不是这个框架本身的设计优劣问题。

3. **用着方便，容易理解**
   这里表达的其实是一种主观感受，而不是可以客观衡量的判据。在不同的技术环境、技术背景下，每个人有着不同的技术偏好，个体感受可能存在着巨大差异。

4. **体积小，速度快**
   这体现的是实现层面优化的结果，未必是框架自身概念设计的一种优势体现。体积小有可能是因为包含的功能特性集比较少导致的，速度快可能是功能特性少的间接结果，也可能是因为使用了某些奇技淫巧（比如大量使用底层不稳定、未公开的API）。

## 存在客观的评判标准吗？

软件开发虽然是一种实践性很强的技术活动，但是在科学昌明的今天，它显然也不应该是一种完全依赖于经验积累的玄学。我们可以从计算机科学的基本原理--信息论的角度出发给出一些客观的评判标准。

## 一. 业务开发能否独立于框架？

近二十年以来，软件框架技术领域的一个非常重要的进展就是认识到框架的中立性（framework agnostic）。业务代码开发本质上是对业务信息的一种表达，这种表达原则上应该独立于任何软件框架，甚至是独立于任何技术因素。**框架的作用是辅助我们用最自然、最直观的方式来表达业务信息，同时能够满足性能和其他技术规范要求**。**最理想的框架，应该是在开发业务代码时完全意识不到它存在的框架**。

这样做的好处在于我们可以**避免业务域和纯技术域之间的信息渗透和污染**，使得业务代码的测试、技术框架的升级甚至更换变得异常简单。

### POJO和轻量级框架

轻量级框架的星星之火是从反对EJB这种重型容器技术开始燃起的。传统的框架往往关注重点是提供功能特性，在使用框架的过程中不可避免的在业务代码中会引用框架特有的对象、插入框架特有的函数调用等，这往往使得业务功能的实现与具体运行环境强绑定，甚至和框架的某个版本强绑定。而轻量级框架首次推广了POJO的概念，在编写一般性的业务代码时，业务代码所操纵的对象在形式上就是没有任何框架依赖的普通Java对象。比如Spring1.0的配置中，Bean的实现完全不需要具有Spring框架的知识，在beans.xml配置文件中即可实现任意复杂的bean的装配。

在这一观点下，传统的SpringMVC框架的如下设计是不合适的，

```javascript
  public void myMethod(HttpServletRequest request){
      ...
  }

  @RequestMapping("/test")
  public ModelAndView test(){
     ModelAndView mav=new ModelAndView("hello");
     mav.addObject("time", new Date());
     mav.getModel().put("name", "caoyc");

     return mav;
  }
```

在新的Web框架设计中，一般都是接收POJO对象，返回POJO对象，除了函数上的注解之外没有任何框架依赖的痕迹，而注解本身又只是一些纯粹的描述性信息

```javascript
    @POST
    @Path("/my-method2")
    ApiResponse<MyResponseBean> myMethod2(MyRequestBean requestBean){
        ...
    }
```

> 注解可以用最小化的、框架中立的方式引入所需信息。例如在Dao访问层，现在大量的ORM框架都可以识别JPA注解，这使得同一个实体定义可以应用于多种ORM框架。

依赖于框架特有的接口仅仅是一个表象，本质上它导致的问题是通过接口依赖间接使得业务逻辑与某种特定的运行时技术环境产生耦合。比如，它导致在Controller中编写的代码只能用于Web请求处理，不能直接在二进制RPC层复用，也不能直接作为消息队列处理函数被复用。

在Nop平台中，NopGraphQL框架将请求参数规范化为一个JSON对象，而不需要像传统Web框架那样引入传参的N种方式（通过param传，通过restPath传，通过cookie传等）。这使得业务代码脱离了对Web运行环境的依赖，同样的服务函数可以直接注册为Kafka消息队列的响应函数，或者批处理文件的处理函数，进行自动化测试的时候也不需要Mock Web服务器，测试成本大为降低。例如，只要我们提供了在线的针对单个账户的入账服务，则无需编程，通过简单配置就可以得到一个每晚定时运行的读取批处理文件执行的批量入账服务。

> 前端Redux和Vuex框架本质上也都是将action规范化为针对单个POJO对象的单参数函数。

### 虚拟DOM和Hooks

在前端领域越来越多的业务逻辑也开始采用框架中立的表达形式。

例如，虚拟DOM概念的引入使得前端框架可以脱离浏览器运行环境，这造就了React Native这种多端统一的开发技术和各类小程序框架的繁荣。虚拟DOM本质上就是普通的JavaScript对象，不同的运行时都可以创建并且翻译同样的虚拟DOM。

Hooks概念的发展使得前端界面逻辑的表达可以摆脱组件对象的形式束缚，只需要引入最小化的Hooks假定，就可以将业务逻辑抽象到框架无关的纯逻辑函数中。借助于Hooks这样的抽象，Headless的组件库逐渐开始占据主流，并且可以用同一套核心代码适配React/Vue/Angular等多种基础框架。

> 传统上前端编程总是依附于某种组件框架，代码需要作为Component类的成员属性和成员函数，这导致代码与某种特定的组件语法、特定的组件运行时形成耦合。

## 二. 框架进行了哪些自动推导？

一个框架如果具有本质上的优越性，那么它一定是**相比于其他可选方案更充分的利用了某些信息，并且基于这些信息自动推导完成了大量的工作**。

### 注解 vs. XML配置

这些年来XML配置日渐式微，取而代之的一般是注解技术。注解相对于XML配置最重要的优点是它**依附于程序语言的语法结构**，在强类型语言中还可以利用已经存在的类型信息，从而极大降低了需要表达的信息量。
如果使用XML配置，大量的工作其实是在搭建基本的对象结构，这不仅导致重复工作，同时还带来了XML配置结构和对象结构之间的同步问题。特别是当代码重构的时候，注解可以利用IDE已有的重构能力，而XML配置则往往游离于重构工具之外。

有趣的是，在重度模型驱动的低代码场景下，情况又有了新的变化。在手工编写代码的情况下，代码是最可靠的信息源，一切其他信息都是从代码信息衍生得到。而**在模型驱动的场景下，模型是Unique Source of Truth**，
代码也只是根据模型衍生得到的一种信息表达形式。当需要重构的时候，我们只需要修改模型，自动就可以实现代码以及相关配置的修改。在这种情况下，就不存在XML重复表达信息，以及XML与代码之间信息需要同步的问题了。

XML相比于注解并不是一无是处，它有着自己独特的优势，在模型驱动的场景下这种优势得到放大，因此在Nop平台中我们的做法是以XML配置为基础，以注解为次要的补充形式。在后面我还会详细解释其中的原因。

### ORM中的自动推理

Hibernate出道即巅峰，它确立了所谓ORM框架这个品类的基本形态（后续的ORM框架一般只是Hibernate的简化版），这其中的价值就在于ORM隐含进行了如下推导:

1. 自动实现数据库记录和Java对象之间的相互映射，自动实现字段类型转换，自动生成实体主键。

2. 按照主键自动进行缓存。如果两次查询结果中包含同样的实体对象，则始终会返回同样的Java对象。这既提升了性能，又在某种意义上提升了应用的事务隔离级别：可以做到某种类似Repeatable Read的效果。

3. 通过dirty检查识别对POJO属性的修改，自动生成insert/update语句，无需手工调用dao.update(entity)操作，而且可以自动利用JDBC batch机制进行性能优化。

4. 借助Spring的声明式事务机制实现自动的事务提交和失败回滚。

5. 关联对象自动实现延迟加载

6. 自动利用外键关系推导出多表关联条件，比如where  a.b.c = 3会自动利用a和b的关联条件以及b和c的关联条件自动生成a、b、c这三个表的关联条件。如果前台已经实现了针对单表的查询显示，则将字段名修改为a.b.c就自动可以实现多表联合查询。

NopORM框架引入了更多的自动推理：

1. 自动将纵表转换为虚拟的横表：扩展字段可以保存在(entityName,entityId,fieldName,fieldValue)这样的纵表中，但是程序中使用时与数据库原生字段相同，同时可以通过SQL语法实现对扩展字段的查询和排序。

2. 自动跟踪实体属性修改，记录修改日志，并与自动化测试框架集成，提供数据库层面的录制回放。

3. 与NopGraphQL引擎结合，自动将领域模型发布为GraphQL服务。

### 自动推导的陷阱

之所以能够自动推导，肯定是因为引入了某些额外的假设。如果这些假设偏离了实际情况，那么就可能产生反效果。比如说，

1. Hibernate可以设置关联属性eager加载，但是并不是每次请求数据都必然需要加载关联数据，这可能导致不必要的性能损耗。NopOrm框架选择了所有关联属性都延迟加载，需要优化加载时再使用BatchLoadQueue机制。

2. Hibernate可以设置session的FlushMode为auto，由框架根据情况判断是否自动将修改刷新到数据库中。但这一机制的使用经常导致意料之外的数据库操作，很小的程序改动就可能导致应用性能大幅下降。NopOrm框架选择取消这种自动机制，只支持手动flush。

## 三. 框架提供了哪些自动转换？

这里的自动转换指的是信息的核心内容并没有发生变化，只是从一种表示形式转换为另一种表示形式。自动转换本质上是信息自动推导的一个特例，但是因为它的设计非常通用，所以值得单独强调一下。

最典型的例子是Web框架中常用的JSON转换：Java对象和JSON文本之间的双向转换。早期的Web框架缺乏规范化的复杂参数编码方案，很多情况下我们需要手工编程解析前台发送的请求参数。而现在JSON序列化已经发展成为一个脱离Web环境的通用结构转换方案。

因为自动的双向转换意味着信息总量保持不变，所以一般情况下它都是与具体业务无关的一种通用机制。从数学上说，这意味着对于结构空间A中的**每一个结构**，我们都可以在结构空间B中找到它的一个对应结构（或者一组等价的对应结构），反之对于结构B中的每一个结构，我们也能够在结构空间A中找到它的对应。

自动转换可以很自然的连接在一起形成更为复杂的复合转换。

```
  A <=> B <=> C
```

需要强调的是，这种转换是通用的，也就是说**不存在特例，每一个可能会遇到的结构都能够被转换**。而我们手工针对业务场景进行编程时使用的转换方式一般都是AdHoc的特殊实现，无法自动处理其他业务场景下的情况。

基于可逆计算原理，Nop平台将自动转换的设计思想贯彻到了平台的方方面面：

1. 实现XML和JSON之间的自动转换。因此我们可以使用XML、JSON、YAML等多种文件格式编写AMIS页面代码（AMIS框架本身只支持JSON格式）。

2. 实现XML和领域对象之间的双向转换。因为我们只要定义XDef元模型就可以自动实现领域模型解析、验证、断点调试等功能。

3. 实现Excel和领域对象之间的双向转换。因此我们可以使用Excel格式的模型文件来设计平台中的所有领域模型，而无需特殊编写模型解析代码。比如，我们可以根据XML格式的ORM模型定义自动得到Excel格式的数据模型定义，或者反之。

4. 实现可视化编辑模型和领域对象之间的双向转换。只要通过简单描述即可自动实现领域对象的可视化编辑。比如，工作流的设计器和ORM设计器都可以根据领域模型的定义自动推导得到。

```
  YAML <=> JSON  <=> XML  <=> DomainObject <=> Excel <=> VisualModel
```

      反观MyBatis、Hibernate、Spring等框架，它们对于模型文件的解析都是手工实现的，IDE插件和可视化编辑器等都需要单独去编写维护。

还有一些双向转换并不是完全的信息等价，我们可以在转换过程中补充一些额外的信息。比如前台提交的请求数据可以被自动转换为数据库实体对象，从而实现主子表数据的保存和修改，但是这个转换过程需要进行数据有效性检查和权限检查，执行类型转换、格式转换等操作。另一个方向，取出数据库实体之后我们可以将它自动转换为JSON数据返回给前台，这个过程同样需要进行权限检查和格式转换。NopGraphQL引擎通过引入Meta对象来补全信息差，从而将复杂业务对象的增删改查操作自动化。

```
  JSON + Meta => Entity
  Entity + Meta => JSON
```

### 四. 在框架之外如何使用相关信息？

传统上的框架设计只关注自身的功能特性，对于框架在外部信息网络中的位置和交互价值往往并不关心。但随着软件领域智能化程度的提高，我们希望推动信息在各个层面、各个组件之间无阻碍的自由流动，此时必须要考虑框架和外部信息网络如何相互作用的问题。

### 独立存在的模型信息

有一定复杂度的框架一定会建立自己的领域模型，并在内部大量使用可配置的模型信息，比如Hibernate内部使用的EntityModel，Spring内部使用的BeanDefinition模型等，但是在很多框架中，模型信息都只具有内部表现形态，与框架的运行时紧密纠缠在一起，外部系统无法通过很简单的方式来复用这些模型信息。

Hibernate在6.0之后逐步开始废弃hbm配置文件，仅保留注解作为实体模型定义方式，这在某种程度上是一种倒退。

1. 它使得Hibernate所定义的实体模型被封闭在Hibernate框架之内，如果不使用Hibernate内部的实现我们很难获取到对应的模型信息，而通过Hibernate内部函数获取到的模型对象上也有可能不是纯粹的描述性信息，而是混杂着与Hibernate的运行时实现相关的其他信息。

2. 如果我们选择直接通过反射去解析注解信息，同样会依赖于Java语言内置的机制，同时我们需要进行过滤筛选，屏蔽Java对象上与实体模型不相关的信息，比如需要忽略transient字段，忽略不相关的其他注解等。也就是说我们需要重新去发现**模型信息**，而无法直接获取到一个解析好并通过验证的模型对象

与Hibernate的做法相反，Nop平台强调模型信息的独立性，**模型信息以XML文件为载体**。这样其他的语言或者框架可以在完全不依赖Nop平台的情况下自由的利用这些模型信息。比如代码生成器可以直接读取XML模型文件，生成前后端代码，生成Word或者Excel文档等。

> 模型对象序列化为模型文件，而模型文件必然具有某种约定的Schema结构，它可以被看作是一种特定的语法定义，即所谓的领域特定语言（DSL）。

### 独立诊断和调试

另外一种常见的情况是框架内部存在一个非常复杂的模型构造过程，比如SpringBoot框架内部会执行复杂的条件判断，最终起作用的具体是哪些Bean的定义并没有一个直观的展现，这导致出现问题的时候难以进行诊断。这很自然的引出一个问题：如果完全不了解框架的执行细节，**从纯粹的外部视角去观察系统**，我们可以得到哪些信息？

Nop平台基于可逆计算原理，广泛使用DSL来定义和描述系统功能，它明确区分了编译期和运行期，将尽可能多的与运行时无关的计算下放到编译期执行，通过所谓的元编程（Meta Programming）来完成模型信息的动态发现和组装。

例如，NopIoC框架在启动时会执行类似SpringBoot的动态条件判断，得到的结果是一个统一的BeansModel。在调试模式下，框架会自动输出一个合并后的模型文件到`_dump`目录下，同时我们也可以通过`/p/DevDoc__beans`这种的REST服务来获取到对应的模型定义。返回的模型定义采用Spring1.0语法格式，相当于是NopIoC通过元编程将具有动态条件的模型语法化归为简单的Spring1.0语法。通过合并后的beans.xml文件，我们就可以直观的理解当前系统中实际被启用的bean的定义情况。

Nop平台中所有的模型对象都可以自动的转换为对应的DSL模型文件。这些模型文件完全没有运行时的状态约束，可以被直观的理解。系统执行异常时，Nop平台也会尽量将异常对应于DSL所在的源码位置。

### 信息管道

如果我们跳出框架自身，从更广阔的信息传递网络角度去观察，我们会发现框架不仅仅是信息的源（Source）和汇（Sink），很多时候它也需要作为信息的通道，也就是说某些信息并不由框架产生，也不由它消费，但是可能需要经过它传递给其他的结构（特别是在分层架构中框架处于某一抽象层面时，原则上需要避免跨层面直接交互）。

在Nop平台的设计中，所有的DSL和所有的实体对象都自动支持扩展属性，可以存放当前框架不会直接用到的扩展信息。从而在整个框架中始终维持着一条扩展信息通路。这是一种全局性的设计，它意味着在系统各处都需要有额外的信息存放空间，比如经过消息队列发送信息时，我们要求消息必须支持header集合，可以通过header存放额外的信息。

借助于扩展属性，我们在构造如下的软件生产管线就可以在上游指定部分在下游使用的信息：

![](../tutorial/delta-pipeline.png)

在Excel的数据模型中我们可以指定部分显示相关的属性，它经由ORM层的扩展属性传递给Meta，然后再传递到前端页面模型中。

### 五. 框架的设计完备性如何？

从科学的角度上说，一个科学的解决方案绝不会是一个孤立的设计，而必然是包含从简单到复杂的一组可以渐进演化的策略集合，针对不同的复杂性我们都需要拟定对应的解决策略。因此，一个立足于适应各种使用场景的底层框架一定要在某种程度上保证自己的设计完备性，而且这种完备性往往无法用穷举法来实现。

### 函数抽象和模板化

举一个简单的例子。假设我们现在要编写一个流程设计器，流程节点需要显示图标和文字，最简单的设计如下：

```javascript
type Node{
    icon: string;
    label: string;
    ...
}
```

如果我们需要控制文字的显示位置，则还需要加入labelPosition这样的描述字段。如果要求根据流程状态的不同改变背景颜色或者显示额外的一个状态标识，则我们需要继续为节点增加statusIconMapping等属性。显然我们无法通过属性枚举来穷尽所有可能的需求，为了保证设计的完备性，我们必须要引入函数抽象，例如在节点级别提供一个渲染函数render，它负责实现节点的自定义渲染。

一旦建立函数抽象，进一步的问题就是如何去实现这个函数的问题。一个有趣的解决方案是模板化。

```xml
<template>
  <span v-if="prop.label">{{prop.label}}</span>
  <span :class="'status-'+prop.status" />
</template>
```

**借助于一种图灵完备的模板语言，我们可以用描述式的方式实现对函数的分解，甚至可以提供一种可视化的设计器来支持客户自行设计函数内容**。

Nop平台系统化的利用Xpl模板语言来实现对函数的细粒度分解。比如在报表引擎中我们需要连接外部的数据源来获取数据

```xml
<beforeExecute>
    <report:UseSplDataSet src="/report.splx" var="ds1" xpl:lib="/report.xlib" />
</beforeExecute>
```

一般的报表引擎总是内置大量的数据源种类，试图穷尽所有外部数据源的连接方式。如果一种数据源没有内置在报表工具中，我们就需要等待报表工具厂商支持或者需要自行编写一个插件。在NopReport引擎中，数据源的概念并不是内置在引擎深处的一个概念，引擎只是在输出报表时调用一个beforeExecute函数来准备数据。具体如何获取数据我们是在beforeExecute模板函数中自行决定的。Nop平台提供了所谓的Xpl模板语言，它是一种采用XML语法格式、图灵完备的程序语言，除了手工编写之外，我们也可以通过可视化设计器来设计Xpl模板。

> Xpl模板语言集成了NopIoC依赖注入容器，借助于IoC容器的对象发现能力（可以按照名称、前缀、注解、类型等多种方式动态查找所有满足条件的bean，并实现自动注入），可以起到类似插件机制的作用，而且调用形式更加简单、直观。

### 分层次、分阶段设计

一个完备的解决方案必然是高度结构化的，在不同颗粒度的结构层次我们都应该提供**仅需要使用该层次信息的处理机制**。以NopORM框架为例

1. IOrmTypeHandler：负责处理单个字段层面的结构问题。比如说字段加密、字段类型适配等。

2. IOrmComponent: 负责处理多个字段层面的结构问题。比如将多个字段组织成一个可复用的Address类型等。

3. IOrmEntity: 负责处理单个实体对象层面的结构问题。比如增加实体扩展属性、跟踪实体属性修改等。

4. IOrmInterceptor：拦截所有实体的关键操作，具有全局性的知识。例如录制整个请求过程中所有读取和修改的数据，输出到数据文件中作为自动化单元测试的初始化数据和结果验证数据。

除了组合关系之外，结构之间还可以存在着丰富的变换关系。我们需要仔细的对结构层次进行梳理，确定哪些是最基本的概念，哪些可以成为衍生概念。而不是所有的特性都堆砌在一起，形成一种平铺式的设计。

比如在NopORM框架中，多对多关联并不是一个内置的概念，底层的关系数据库存储机制只处理一对多和多对一关联。在Java实体层面，通过代码生成机制生成一些辅助函数，将多对多关联分解为两个一对多关联。类似的处理还有NopWorkflow中对于会签功能的实现：工作流的运行时引擎并不需要内置会签节点，通过`x:gen-extends`这种嵌入式代码生成器，在工作流模型加载的过程中可以动态生成DSL代码，将一个会签节点展开成为一个普通步骤节点和一个Join汇聚步骤节点。

Nop平台基于可逆计算原理，提供了一种系统化的多阶段编译机制。类似的处理机制可以应用于所有的自定义DSL语言。参见 [XDSL：通用的领域特定语言设计](https://zhuanlan.zhihu.com/p/612512300)

### 异步处理

同步处理和异步处理看似只是技术层面的一种选择，但本质上它们对应着不同的世界观。一个没有考虑异步处理的框架设计是不完整的。

在目前的程序实践中，如果事前没有考虑支持异步处理，则往往后期很难将整体框架转换为支持异步处理。为了系统化的支持异步处理，我们需要考虑如何将一个上下文对象在各个线程间进行传递的问题，同时也需要考虑各种并发场景下如何避免锁冲突等复杂的技术问题。

## 六. 框架提供了哪些差量化机制？

所有的框架都要考虑可扩展性的问题。在软件开发中，所谓的可扩展性指的是在不需要修改原始代码的情况下，通过添加额外的代码或差异信息，可以满足新的需求或实现新的功能。如果在完全抽象的数学层面去理解软件开发中的扩展机制，我们可以认为它对应于如下公式：

```
 Y = X + Delta
```

* X对应于我们已经编写完毕的基础代码，它不会随需求的变化而变化
* Delta对应于额外增加的配置信息或者差异化代码

在这个视角下，所谓的可扩展性方面的研究就等价于Delta差量的定义和运算关系方面的研究。

现有的框架技术所使用的扩展机制存在如下问题：

1. **需要事先预测在哪些地方可能会进行扩展，然后在基础代码中定义好扩展接口和扩展方式**
2. **每一个组件能够提供哪些扩展方式和扩展能力都需要单独去设计，每个组件都不一样**
3. **扩展机制往往会影响性能，扩展点越多，系统性能越差**

以Hibernate中增加Gis扩展为例，需要实现ContributorImplementor接口，实现contributionFunctions等函数，在其中注册GIS相关函数。

```
        @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        HSMessageLogger.SPATIAL_MSG_LOGGER.functionContributions( this.getClass().getCanonicalName() );

        KeyedSqmFunctionDescriptors functionDescriptors;
        if ( useSTGeometry ) {
            functionDescriptors = new OracleSQLMMFunctionDescriptors( functionContributions );
        }
        else {
            functionDescriptors = new OracleSDOFunctionDescriptors( functionContributions );
        }
        SqmFunctionRegistry functionRegistry = functionContributions.getFunctionRegistry();
        functionDescriptors.asMap().forEach( (key, funcDescr) -> {
            functionRegistry.register( key.getName(), funcDescr );
            key.getAltName().ifPresent( altName -> functionRegistry.register( altName, funcDescr ) );
        } );

        } sdoGeometryType );
    }
```

而**在Nop平台中我们可以通过统一的Delta定制来实现扩展**。具体介绍参见[如何在不修改基础产品源码的情况下实现定制化开发](https://zhuanlan.zhihu.com/p/628770810)

### 七. 整体代码量是否更小？

一个可以进行客观度量的标准是：使用框架之后，代码量是否发生了下降？代码量可以看作是对系统复杂性的一种度量（所谓的描述复杂性）。一个有价值的框架它应该可以降低系统的复杂度，从而使得代码量出现明显的下降。

这里容易出现误导的是，我们必须考虑模型和应用两者的代码量之和：

```
 整体复杂度 = 模型复杂度 + 应用复杂度
```

很容易想见，我们可以实现一个非常复杂的模型，把所有常见的业务需求都做成开关（穷举法），这样只要输入少量应用描述即可得到实现应用系统。另一个极端是，我们可以实现一个功能非常贫瘠的模型，然后完全依靠在应用层编码来实现业务需求。显然，我们需要实现这两者之间的平衡，让模型的复杂度与具体业务应用的复杂度相匹配，这样才可以保证模型可以在尽可能多的业务场景中发生作用（提升模型的泛化性能）。

### 八. 框架的设计是阻碍还是促进了性能优化？

如果一个框架提升了业务表达的抽象层次，那么原则上它应该扩大了性能优化的空间，允许使用更多的性能优化手段。

比如在ORM框架中，通过session统一管理实体更新可以很容易的引入JDBC Batch优化，而且在更新数据库的时候可以自动按照表的依赖关系和主键大小进行排序，确保总是按照同样顺序更新数据库，减少出现数据库死锁的情况。

> 死锁产生的原因一般是线程A先更新记录x，再更新记录y，而另一个线程先更新记录y，再更新记录x，解决的方法是对更新操作进行排序，总是按照同样的顺序进行更新，比如所有的线程都固定为先更新x，再更新y。

对Hibernate的性能一个常见的诟病是一旦使用Hibernate出现性能问题，往往难以通过局部调整进行优化，很多时候都需要重写代码，而且重写后的代码看起来很不符合Hibernate的常见使用范式。这意味着Hibernate的常见使用范式和性能优化是冲突的，而且难以进行后期补救。比如说Hibernate中老大难的N+1问题，

```java
for(MyEntity entity: entityList){
    for(MyChild child: entity.relatedChildren){
        ...
    }
}
```

使用Hibernate的标准范式是遍历关联对象集合来执行操作，而每次访问关联集合都可能导致一次单独的数据库查询操作。在前期数据量比较小的情况下，直接通过集合属性获取可以避免额外的DAO调用，显得非常简单直观，但是如果发现性能有问题，就必须修改获取数据的方法，不能再使用属性集合直接获取。

NopORM框架通过引入类似GraphQL DataLoader的BatchLoadQueue机制解决了这个问题。具体来说，在需要优化数据库加载的业务代码前面插入BatchLoad调用即可。

```java
dao.batchLoadProps(entityList,Arrays.asList("relatedChildre.otherRelated","relatedEntity"));
```

> 在低代码开发的场景下，甚至可以由低代码平台收集运行时数据访问信息，然后自动在合适的地方插入性能优化指令。

很多框架随着功能特性集的不断增加，在运行时需要执行大量的特性选择这样的无效操作，性能不可避免的出现劣化。Nop平台综合利用代码生成、编译期变换、宏函数等机制尽量在开发期或者模型初始化阶段执行大量的特性选择逻辑，确保仅有必要的逻辑在运行时执行。

## 总结

* 一个框架相当于是建立一个有独立存在意义的技术空间，它所提供的各种能力相当于是这个空间中定义的运算规律（数学定理）。

* 类似于数学定理的推导，在越少的假设上进行的推导（摆脱对具体业务上下文的依赖）可以应用到越广泛的场景中。

* 自动推导的结果可以像数学定理那样复合起来，得到新的结果。

* 可逆计算理论提供了对设计完整性的一种新的评估视角。

* 元模型、元编程、模板语言应该成为框架设计工具箱中的必备工具。

基于可逆计算理论设计的低代码平台NopPlatform已开源：

- gitee: [canonical-entropy/nop-entropy](https://gitee.com/canonical-entropy/nop-entropy)
- github: [entropy-cloud/nop-entropy](https://github.com/entropy-cloud/nop-entropy)
- 开发示例：[docs/tutorial/tutorial.md](https://gitee.com/canonical-entropy/nop-entropy/blob/master/docs/tutorial/tutorial.md)
- [可逆计算原理和Nop平台介绍及答疑\_哔哩哔哩\_bilibili](https://www.bilibili.com/video/BV1u84y1w7kX/)
