
package com.github.shawven.security.verification.captcha;

import com.github.shawven.security.verification.VerificationGenerator;
import com.github.shawven.security.verification.VerificationRequest;
import com.github.shawven.security.verification.config.CaptchaConfiguration;
import org.apache.commons.lang3.RandomStringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 默认的图片验证码生成器
 *
 */
public class CaptchaGenerator implements VerificationGenerator<Captcha> {

	/**
	 * 系统配置
	 */
	private CaptchaConfiguration configuration;


    public CaptchaGenerator(CaptchaConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
	public Captcha generate(VerificationRequest<Captcha> request) {
        CaptchaRequest cReq = (CaptchaRequest) request;
        int length = cReq.getLength() != null ? cReq.getLength() : configuration.getLength();
        int width = cReq.getWidth() != null ? cReq.getWidth() : configuration.getWidth();
		int height = cReq.getHeight() != null ? cReq.getHeight():  configuration.getHeight();
		int expireIn = cReq.getExpireIn() != null ? cReq.getExpireIn():  configuration.getExpireIn();

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		int fontSize = 20;

		Graphics g = image.getGraphics();
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("Times New Roman", Font.ITALIC, fontSize));
		g.setColor(getRandColor(160, 200));

        Random random = new Random();
		for (int i = 0, lineNum = (int)Math.sqrt(width * height); i < lineNum; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = x + random.nextInt(width / 3);
			int yl = y + random.nextInt(height / 3);
			g.drawLine(x, y, xl, yl);
		}

        String randomString = getRandomString(length);
		int unitWidth = 13;
		int x = width / 2 - (unitWidth * length) / 2;
		int y = (height + fontSize / 2) / 2;

		for (int i = 0; i < length; i++) {
			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            x += i == 0 ? 0 : unitWidth;
			g.drawString(String.valueOf(randomString.charAt(i)), x, y);
		}

		g.dispose();

		return new Captcha(image, randomString, expireIn);
	}

	/**
	 * 生成随机背景条纹
	 *
	 * @param fc
	 * @param bc
	 * @return
	 */
	private Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	private String getRandomString(int length) {
        String str;
        do {
            str = RandomStringUtils.randomAlphanumeric(length);
            // 排除I l 1 这种模糊不清的
        } while (str.contains("I") || str.contains("l") || str.contains("1"));
        return str;
    }
}
