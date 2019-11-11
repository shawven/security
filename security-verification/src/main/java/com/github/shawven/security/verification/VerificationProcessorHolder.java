
package com.github.shawven.security.verification;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * 校验码处理器管理器
 */

public class VerificationProcessorHolder {

    private Map<VerificationType, VerificationProcessor> verificationProcessors = Collections.emptyMap();

    public VerificationProcessorHolder(List<VerificationProcessor> verificationProcessors) {
        if (verificationProcessors == null) {
            return;
        }
        this.verificationProcessors = verificationProcessors.stream()
                .collect(toMap(
                        processor -> {
                            String className = processor.getClass().getSimpleName();
                            String typeName = StringUtils.substringBefore(className, "Processor").toUpperCase();
                            return VerificationType.valueOf(typeName);
                        },
                        identity())
                );
    }

    /**
     * @param type
     * @return
     */
    public VerificationProcessor get(VerificationType type) {
        VerificationProcessor processor = verificationProcessors.get(type);
        if (processor == null) {
            throw new VerificationException(type.getName() + "发起校验请求错误");
        }
        return processor;
    }
}
