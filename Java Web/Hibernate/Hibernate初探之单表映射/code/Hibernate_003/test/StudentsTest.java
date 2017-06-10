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
		//´´½¨ÅäÖÃ¶ÔÏó
		Configuration configuration = new Configuration().configure();
		//´´½¨»á»°¹¤³§¶ÔÏó
		sessionFactory = configuration.buildSessionFactory();
		//´´½¨»á»°¶ÔÏó
		session =sessionFactory.openSession();
		//¿ªÆôÊÂÎñ
		transaction = session.beginTransaction();
	}

	@After
	public void destory() {
		transaction.commit();// Ìá½»ÊÂÎñ
		session.close();// ¹Ø±Õ¶Ô»°
		sessionFactory.close();// ¹Ø±Õ»á»°¹¤³§
	}

	@Test
	public void testSaveStudents() {
		//Students s = new Students(1, "ÕÅÈý·á", "ÄÐ", new Date(), "Îäµ±É½");
		Students s = new Students();
		s.setSname("ÕÅÈý·á");
		//s.setSid(1); //nativeµÄÊ±ºòÒª×¢ÊÍµô
		s.setGender("ÄÐ");
		s.setBirthday(new Date());
		//s.setAddress("Îäµ±É½");
		Address address = new Address("710068","2883232","ËÕÖÝÊÐ");
		s.setAddress(address);
		session.save(s);// ±£´æ¶ÔÏó½öÊý¾Ý¿â

	}
	@Test
	public void testWriteBlob() throws Exception{

		Students s = new Students(1, "ÕÅÈý·á", "ÄÐ", new Date(), "Îäµ±É½");
		//先获得照片文件
		File file = new File("D:"+ File.separator + "23tree.png");
		//获得照片文件的输入流
		InputStream inputStream = new FileInputStream(file);
		//创建一个Blob对象
		Blob image = Hibernate.getLobCreator(session).createBlob(inputStream,inputStream.available());
		//设置照片属性
		s.setPicture(image);、
		//保存学生
		session.save(s);
		
	}
	@Test
	public void testReadBlob() throws Exception{
		Students s = session.get(Students.class, 1);
		Blob image = s.getPicture();
		InputStream input = image.getBinaryStream();
		File f = new File("D:"+ File.separator + "dest.png");
		OutputStream output = new FileOutputStream(f);
		//´´½¨»º³åÇø
		byte[] buff = new byte[input.available()];
		input.read(buff);
		output.write(buff);
		input.close();
		output.close();
		
	}
}
