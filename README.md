#TinyORM 使用说明

TinyORM 的设计目标是作为 SQLiteDatabase 的补充工具，使得

* 创建表
* 删除表
* 插入表数据
* 删除表数据
* 查询表数据
* 修改表数据

更简单和易用。

## 设计思路
一个表和一个 Class 对应，Class 中的属性定义和表的列对应，Class 的对象与表的行对应。

* Table <---> Class
* Column <---> Property
* Row <---> Object

表的这些概念通过 Annotation 去定义，示例如下

```
@Entity(name = "table_four_column")
public class TableFourColumn {
    @Property(name = "_id")
    @Id
    private String mId = UUID.randomUUID().toString();

    @Property(name = "column_1")
    private int mColumn1 = 111;

    @Property(name = "column_2")
    @NotNull
    private double mColumn2 = 222.222;

    @Property(name = "column_3")
    private String mColumn3 = "column_3";

    @Property(name = "column_4")
    private String mColumn4 = "column_4";
}
```
上述定义表示 Table 名称为 "table\_four\_column"，有 5 列分别为 "\_id"、"column\_1"、"column\_2"、"column\_3"、"column\_4"，其中 "\_id" 为主键。这些设计与 GreenDAO 一致。

## 功能
### 注解
* @Entity：定义表名称、索引
* @Property：定义表的列名
* @Id：定义主键
* @Index：定义索引
* @NotNull：定义不允许为空
* @Unique：定义唯一性

### 使用接口
* createTable：创建表
* deleteTable：删除表
* createIndex：创建索引
* insert：向表中插入一行
* delete：从表中删除一行
* query：从表中查询对应对象
* update：更新表中的一行

具体定义如下：

```
    /**
     * 根据 Bean 定义创建数据库表
     *
     * @param db DBDatabase 引擎
     * @param clazz Bean 对应的 class
     * @return 创建是否成功
     */
    public boolean createTable(SQLiteDatabase db, Class<?> clazz) {}

    /**
     * 根据 Bean 定义删除数据库表
     *
     * @param db DBDatabase 引擎
     * @param clazz Bean 对应的 class
     * @return 删除是否成功
     */
    public boolean deleteTable(SQLiteDatabase db, Class<?> clazz) {}

    /**
     * 根据 Bean 定义创建某个索引
     *
     * @param db DBDatabase 引擎
     * @param clazz Bean 对应的 class
     * @param indexName 指定索引名
     * @return 创建是否成功
     */
    public boolean createIndex(SQLiteDatabase db, Class<?> clazz, String indexName) {}

    /**
     * 根据 Bean 定义创建所有索引
     *
     * @param db DBDatabase 引擎
     * @param clazz Bean 对应的 class
     * @return 创建是否成功
     */
    public boolean createIndex(SQLiteDatabase db, Class<?> clazz) {}

    /**
     * 往数据库表中插入一行
     *
     * @param db DBDatabase 引擎
     * @param object 需要插入的对象
     * @return 新行 id，如果出错，返回 －1
     */
    public long insert(SQLiteDatabase db, Object object) {}

    /**
     * 删除一行
     *
     * @param db db DBDatabase 引擎
     * @param object object 需要删除的对象
     * @return 执行是否成功
     */
    public boolean delete(SQLiteDatabase db, Object object) {}

    /**
     * 判断对象是否存在数据库中
     *
     * @param db db DBDatabase 引擎
     * @param object object 需要判断的对象
     * @return 是否存在
     */
    public boolean exist(SQLiteDatabase db, Object object) {}

    /**
     * 查询一行的信息
     *
     * @param db DBDatabase 引擎
     * @param object 需要查询的旧对象，根据主键来匹配
     * @return 查询到的新对象，如果查不到，返回 null
     */
    public Object query(SQLiteDatabase db, Object object) {}

    /**
     * 根据 Cursor 的值加载对象
     *
     * @param clazz 需要加载的对象 class
     * @param cursor 数据库游标
     * @return 新对象，如果加载失败返回 null
     */
    public Object query(Class<?> clazz, Cursor cursor) {}

    /**
     * 更新数据库中的一行
     *
     * @param db DBDatabase 引擎
     * @param object 需要更新的对象，按主键去匹配
     * @return 受到影响的行数
     */
    public int update(SQLiteDatabase db, Object object) {}

    /**
     * 按条件更新数据库中的一行
     * 如果列集不为 null, 则说明指更新了某些列，如果有需要获取更新后的完整数据，则通过传入的 ResultValue 对象返回
     *
     * @param db DBDatabase 引擎
     * @param object 需要更新的对象
     * @param columns 选中的列集合，如果为 null，则更新所有列
     * @param result 更新成功之后完整的数据 A，如果执行失败，则不设置 B，如果执行成功，columns 为 null, 则返回原对象 C，如果执行成功，columns 不为 null，则调用 query 获取更新后的值
     * @return 受到影响的行数
     */
    public int update(SQLiteDatabase db, Object object, String[] columns,
                      ResultValue<Object> result) {}

    /**
     * 往数据库中插入一行
     * A，如果不存在，与 insert 行为一致
     * B，如果存在，与 update 行为一致
     *
     * @param db DBDatabase 引擎
     * @param object 需要插入的对象
     * @return 执行是否成功
     */
    public boolean insertOrUpdate(SQLiteDatabase db, Object object) {}

    /**
     * 往数据库中插入一行
     * A，如果不存在，与 insert 行为一致
     * B，如果存在，与 update 行为一致
     *
     * @param db DBDatabase 引擎
     * @param object 需要插入的对象
     * @param columns 需要更新的列，如果是 insert，则忽略
     * @param result 更新后的完整结果
     * @return 执行是否成功
     */
    public boolean insertOrUpdate(SQLiteDatabase db, Object object, String[] columns,
                                  ResultValue<Object> result) {}

    /**
     * 往数据库中插入一行
     * A，如果不存在，与 insert 行为一致
     * B，如果存在，则丢弃
     *
     * @param db DBDatabase 引擎
     * @param object 需要插入的对象
     * @return 执行是否成功
     */
    public boolean insertOrDiscard(SQLiteDatabase db, Object object) {}
```

## 示例
见 Test 中

