package util;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
    public static void indicateError(JButton button) {
        Color originalColor = button.getBackground(); // 원래 색상 저장
        button.setBackground(Color.RED); // 버튼 색상을 빨간색으로 변경
        Timer timer = new Timer(200, e -> button.setBackground(originalColor)); // 200ms 후 복구
        timer.setRepeats(false); // 한 번만 실행
        timer.start();
    }
}