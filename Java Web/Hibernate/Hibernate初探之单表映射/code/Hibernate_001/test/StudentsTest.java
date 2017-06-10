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
		Students s = new Students(1, "ÕÅÈý·á", "ÄÐ", new Date(), "Îäµ±É½");
		session.save(s);// ±£´æ¶ÔÏó½öÊý¾Ý¿â

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
		students.setGender("Å®");
		session.update(students);
		System.out.println(students);
	}
	
	@Test
	public void testDeleteStudents(){
		Students students = (Students)session.get(Students.class,0);
		session.delete(students);
	}
	
}
