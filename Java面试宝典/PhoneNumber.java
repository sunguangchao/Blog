package iooc.timer;
/**
 * ��дhashCode��equals�Ĳο�
 * @author huang
 * ����Ϊ��д������дhashCode����ʱ�����ܻῴ��������ʾ�Ĵ��룬
 * ��ʵ���ǲ�̫���ΪʲôҪʹ�������ĳ˷�������������ϣ�루ɢ���룩��
 * ����Ϊʲô������Ǹ�������Ϊʲôͨ��ѡ��31�������ǰ��������Ĵ�
 * ��������Լ��ٶ�һ�£�ѡ��31����Ϊ��������λ�ͼ�������������˷���
 * �Ӷ��õ����õ����ܡ�˵������������Ѿ��뵽�ˣ�31 * num �ȼ���
 * (num << 5) �C num������5λ�൱�ڳ���2��5�η��ټ�ȥ�������
 * ���ڳ���31�����ڵ�VM�����Զ��������Ż���
 *
 */
public class PhoneNumber {
	private int areaCode;
	private String prefix;
	private String lineNumber;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + areaCode;
		result = prime * result
				+ ((lineNumber == null) ? 0 : lineNumber.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PhoneNumber other = (PhoneNumber)obj;
		if (areaCode != other.areaCode) {
			return false;
		}
		if (lineNumber == null) {
			if (other.lineNumber != null) {
				return false;
			}
		}else if (!lineNumber.equals(other.lineNumber)) {
			return false;
		}
		if (prefix == null) {
			if (other.prefix == null) {
				return false;
			}
		}else if (!prefix.equals(other.prefix)) {
			return false;
		}
		return true;
	}

}
