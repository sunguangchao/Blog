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
		//�������ö���
		Configuration configuration = new Configuration().configure();
		//�����Ự��������
		sessionFactory = configuration.buildSessionFactory();
		//�����Ự����
		session =sessionFactory.openSession();
		//��������
		transaction = session.beginTransaction();
	}

	@After
	public void destory() {
		transaction.commit();// �ύ����
		session.close();// �رնԻ�
		sessionFactory.close();// �رջỰ����
	}

	@Test
	public void testSaveStudents() {
		Students s = new Students(1, "������", "��", new Date(), "�䵱ɽ");
		session.save(s);// �����������ݿ�

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
		students.setGender("Ů");
		session.update(students);
		System.out.println(students);
	}
	
	@Test
	public void testDeleteStudents(){
		Students students = (Students)session.get(Students.class,0);
		session.delete(students);
	}
	
}
