import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.concurrent.BlockingDeque;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Blob;

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
		//Students s = new Students(1, "张三丰", "男", new Date(), "武当山");
		Students s = new Students();
		s.setSname("张三丰");
		//s.setSid(1); //native的时候要注释掉
		s.setGender("男");
		s.setBirthday(new Date());
		//s.setAddress("武当山");
		Address address = new Address("710068","2883232","苏州市");
		s.setAddress(address);
		session.save(s);// 保存对象仅数据库

	}
	@Test
	public void testWriteBlob() throws Exception{
		Students s = new Students(1, "张三丰", "男", new Date(), "武当山");
		File file = new File("D:"+ File.separator + "23tree.png");
		InputStream inputStream = new FileInputStream(file);
		Blob image = Hibernate.getLobCreator(session).createBlob(inputStream,inputStream.available());
		s.setPicture(image);
		session.save(s);
		
	}
	@Test
	public void testReadBlob() throws Exception{
		Students s = session.get(Students.class, 1);
		Blob image = s.getPicture();
		InputStream input = image.getBinaryStream();
		File f = new File("D:"+ File.separator + "dest.png");
		OutputStream output = new FileOutputStream(f);
		//创建缓冲区
		byte[] buff = new byte[input.available()];
		input.read(buff);
		output.write(buff);
		input.close();
		output.close();
		
	}
}
