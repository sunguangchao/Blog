## 一 基本的加锁规则

虽然 MySQL 的锁各式各样，但是有些基本的加锁原则是保持不变的，譬如：快照读是不加锁的，更新语句肯定是加排它锁的，RC 隔离级别是没有间隙锁的等等。这些规则整理如下，后面就不再重复介绍了：

- 常见语句的加锁
  - SELECT ... 语句正常情况下为快照读，不加锁；
  - SELECT ... LOCK IN SHARE MODE 语句为当前读，加 S 锁；
  - SELECT ... FOR UPDATE 语句为当前读，加 X 锁；
  - 常见的 DML 语句（如 INSERT、DELETE、UPDATE）为当前读，加 X 锁；
  - 常见的 DDL 语句（如 ALTER、CREATE 等）加表级锁，且这些语句为隐式提交，不能回滚；
- 表锁
  - 表锁（分 S 锁和 X 锁）
  - 意向锁（分 IS 锁和 IX 锁）
  - 自增锁（一般见不到，只有在 innodb_autoinc_lock_mode = 0 或者 Bulk inserts 时才可能有）
- 行锁
  - 记录锁（分 S 锁和 X 锁）
  - 间隙锁（分 S 锁和 X 锁）
  - Next-key 锁（分 S 锁和 X 锁）
  - 插入意向锁
- 行锁分析
  - 行锁都是加在索引上的，最终都会落在聚簇索引上；
  - 加行锁的过程是一条一条记录加的；
- 锁冲突
  - S 锁和 S 锁兼容，X 锁和 X 锁冲突，X 锁和 S 锁冲突；
  - 表锁和行锁的冲突矩阵参见前面的博客 [了解常见的锁类型](https://www.aneasystone.com/archives/2017/11/solving-dead-locks-two.html)；
- 不同隔离级别下的锁
  - 上面说 SELECT ... 语句正常情况下为快照读，不加锁；但是在 Serializable 隔离级别下为当前读，加 S 锁；
  - RC 隔离级别下没有间隙锁和 Next-key 锁（特殊情况下也会有：purge + unique key）；
  - 不同隔离级别下锁的区别，参见前面的博客 [学习事务与隔离级别](https://www.aneasystone.com/archives/2017/10/solving-dead-locks-one.html)；



###where条件，提取规则
SQL语句中where条件，使用提取规则，最终都会被提取到Index key(First Key & Last key),Index Filter与Table Filter之中。

* Index First Key,只是用来定位索引的起始范围，因此只在索引第一次Search Path(沿着B+树的根节点一直遍历，到索引正确的节点位置)时使用，一次判断即可：
* Index Last Key,用来定位索引的终止范围，因此对于起始范围之后读到的每一条索引记录，均需要判断是否已经超过了Index Last Key的范围，若超过，则当前查询结束。
* Index Filter,用于过滤索引查询范围中不满足查询条件的记录，因此对于索引范围中的每一条记录，均需要与Index Filter进行对比，若不满足Index Filter则直接丢弃，继续读取下一条记录。
* Table Filter，则是最后一道where条件的防线，用于过滤通过前面索引的层层考验的记录，此时的记录已经满足了Index First Key与Index Last Key构成的范围，并且满足Index Filter的条件，回表读取了完整的记录，判断完整记录是否满足Table Filter中的查询条件，同样的，若不满足，跳过当前记录，继续读取索引的下一条记录，若满足，则返回记录，此记录满足了where的所有条件，可以返回给前端用户。

注意：
MySQL5.6中引入了Index Condition Pushdown，究竟是将什么Push Down到索引层面进行过滤呢？答案是Index Filter。在MySQL5.6之前，并不区分Index Filter与Table Filter，统统将Index First Key与Index Last Key范围内的索引记录，回表读取完整记录，然后返回给MySQL Server层进行过滤。而在MySQL5.6之后，Index Filter与Table Filter分离，Index Filter下降到InnoDB索引层面进行过滤，减少了回表与返回MySQL Server层的记录交互开销，提高了SQL的执行效率。





reference:

* [解决死锁之路，常见SQL语句加锁分析](https://www.aneasystone.com/archives/2017/12/solving-dead-locks-three.html)
* [SQL中的where条件，在数据库中提取与应用浅析](http://hedengcheng.com/?p=577)
* [InnoDB事务锁简介](https://yq.aliyun.com/articles/4270?spm=a2c4e.11155435.0.0.64bc4f18HG5hBu)
* [MySQL+InnoDB semi-consitent read原理及实现分析](http://hedengcheng.com/?p=220)