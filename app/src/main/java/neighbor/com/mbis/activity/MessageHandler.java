/**
 * 
 */
package neighbor.com.mbis.activity;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *  1. 기능 : 공통 핸들러 클래스
 *  2. 처리 개요 : 
 *     - 공통 핸들러 클래스
 *  3. 주의사항 : 
 *  4. 작성자/작성일 : 이동식 / 2014. 5. 14.
 * ===================================
 *  5. 수정사항
 *  5.1 요구사항 ID :
 *     - 수정자/수정일 : 
 *     - 수정사유/내역 :
 * ===================================
 * </pre>
 * 
 * @author : 이동식
 * @version : v1.0.0
 * @see : 참조
 * @since : J2SE 6.0
 **/
public class MessageHandler extends Handler {
	/** 메시지 처리 대상 약참조 객체 */
	private final WeakReference<SmartServiceHandlerInterface> handlerInterface;

	/**
	 * <pre>
	 * 1. 기능 : MessageHandler 클래스 생성자
	 * 2. 처리개요 : 
	 *     - MessageHandler 클래스 생성자로 변수 초기화 기능을 수행한다.
	 * 3. 주의사항 : 
	 * ===================================
	 * 4. 작성자/작성일 : 이동식 / 2014. 5. 14.
	 * ===================================
	 * 5. 수정사항
	 * 5.1 요구사항 ID :
	 *     - 수정자/수정일 :
	 *     - 수정사유/내역 :
	 * ===================================
	 * </pre>
	 * 
	 * @param handlerInterface
	 *            메시지 처리 대상 객체
	 */
	public MessageHandler(SmartServiceHandlerInterface handlerInterface) {
		this.handlerInterface = new WeakReference<SmartServiceHandlerInterface>(handlerInterface);
	}

	@Override
	public void handleMessage(Message msg) {
		SmartServiceHandlerInterface handlerInterface = this.handlerInterface.get();
		if (handlerInterface != null) {
			handlerInterface.handleServiceMessage(msg);
		}
	}

	/**
	 * <pre>
	 *  1. 기능 : 메시지 처리 인터페이스
	 *  2. 처리 개요 : 
	 *     - 메시지 처리 인터페이스
	 *  3. 주의사항 : 
	 *  4. 작성자/작성일 : 이동식 / 2014. 5. 14.
	 * ===================================
	 *  5. 수정사항
	 *  5.1 요구사항 ID :
	 *     - 수정자/수정일 : 
	 *     - 수정사유/내역 :
	 * ===================================
	 * </pre>
	 * 
	 * @author : 이동식
	 * @version : v1.0.0
	 * @see : 참조
	 * @since : J2SE 6.0
	 **/
	public interface SmartServiceHandlerInterface {
		/**
		 * <pre>
		 * 1. 기능 : 서비스 메시지 처리
		 * 2. 처리개요 : 
		 *     - 서비스 메시지 처리 기능을 수행한다.
		 * 3. 주의사항 : 
		 * ===================================
		 * 4. 작성자/작성일 : 이동식 / 2014. 5. 14.
		 * ===================================
		 * 5. 수정사항
		 * 5.1 요구사항 ID :
		 *     - 수정자/수정일 :
		 *     - 수정사유/내역 :
		 * ===================================
		 * </pre>
		 * 
		 * @param message
		 *            메시지
		 */
		 void handleServiceMessage(Message message);
	}

	/**
	 * <pre>
	 * 1. 기능 : 핸들러 객체 정리
	 * 2. 처리개요 : 
	 *     - 핸들러 객체 정리 기능을 수행한다.
	 * 3. 주의사항 : 
	 * ===================================
	 * 4. 작성자/작성일 : 이동식 / 2014. 7. 16.
	 * ===================================
	 * 5. 수정사항
	 * 5.1 요구사항 ID :
	 *     - 수정자/수정일 :
	 *     - 수정사유/내역 :
	 * ===================================
	 * </pre>
	 */
	public void clear() {
		handlerInterface.clear();
	}
}
