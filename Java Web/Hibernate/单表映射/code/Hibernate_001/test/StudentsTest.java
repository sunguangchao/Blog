import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StudentsTest {

	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;

	@Before
	public void Init() {
		//创建配置对象
		Configuration configuration = new Configuration().configure();
		//创建会话工厂对象
		sessionFactory = configuration.buildSessionFactory();
		//创建会话对象
		session =sessionFactory.openSession();
		//开启事务
		transaction = session.beginTransaction();
	}

	@After
	public void destory() {
		transaction.commit();// 提交事务
		session.close();// 关闭对话
		sessionFactory.close();// 关闭会话工厂
	}

	@Test
	public void testSaveStudents() {
		Students s = new Students(1, "张三丰", "男", new Date(), "武当山");
		session.save(s);// 保存对象仅数据库

	}
	@Test
	public void testGetStudents(){
		Students students = (Students)session.get(Students.class,0);
		System.out.println(students);
	}
	
	@Test
	public void testLoadStudents(){
		Students students = (Students)session.load(Students.class,0);
		System.out.println(students);
	}
	
	@Test
	public void testUpdateStudents(){
		Students students = (Students)session.get(Students.class,0);
		students.setGender("女");
		session.update(students);
		System.out.println(students);
	}
	
	@Test
	public void testDeleteStudents(){
		Students students = (Students)session.get(Students.class,0);
		session.delete(students);
	}
	
}
