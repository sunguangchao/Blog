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
		//Students s = new Students(1, "������", "��", new Date(), "�䵱ɽ");
		Students s = new Students();
		s.setSname("������");
		//s.setSid(1); //native��ʱ��Ҫע�͵�
		s.setGender("��");
		s.setBirthday(new Date());
		//s.setAddress("�䵱ɽ");
		Address address = new Address("710068","2883232","������");
		s.setAddress(address);
		session.save(s);// �����������ݿ�

	}
	@Test
	public void testWriteBlob() throws Exception{
		Students s = new Students(1, "������", "��", new Date(), "�䵱ɽ");
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
		//����������
		byte[] buff = new byte[input.available()];
		input.read(buff);
		output.write(buff);
		input.close();
		output.close();
		
	}
}
