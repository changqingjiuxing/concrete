package cc.coodex.concrete.support.jsr311;

import cc.coodex.concrete.api.ConcreteService;
import cc.coodex.concrete.jaxrs.AbstractJAXRSResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * Created by davidoff shen on 2016-11-24.
 */
public abstract class AbstractJSR311Resource<T extends ConcreteService>
        extends AbstractJAXRSResource<T> {

    private final static Logger log = LoggerFactory.getLogger(AbstractJSR311Resource.class);

    @Override
    protected int getMethodStartIndex() {
        return 1;
    }

    private void setPriority(int priority) {
        try {
            Thread.currentThread().setPriority(priority);
        } catch (Throwable th) {
            log.warn("Unable set priority: {} ", th.getLocalizedMessage());
        }
    }


    /**
     * 由于javassist无法创建匿名类，因此利用反射找到指定方法进行调用
     * <p>
     * 已知问题：重载且参数数量相同的无法区分
     * <p>
     *
     * @param methodName
     * @param params
     * @return
     */
    protected Object __execute(final String methodName, final String tokenId, final Object... params) {


        final Method method = findMethod(methodName, null);
        int currentThreadPriority = Thread.currentThread().getPriority();
        int methodPriority = getPriority(method);

        try {
            if (currentThreadPriority != methodPriority) {
                setPriority(methodPriority);
            }
            return invokeByTokenId(tokenId, method, params);
        } finally {
            if (currentThreadPriority != Thread.currentThread().getPriority())
                setPriority(currentThreadPriority);
        }
    }



    protected Object execute(final String methodName, final String tokenId, Object... objects) {
        return __execute(methodName, tokenId, objects);
    }


}
