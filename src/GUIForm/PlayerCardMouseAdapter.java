package GUIForm;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PlayerCardMouseAdapter extends MouseAdapter { // 玩家当前持有卡牌的鼠标监听器
    CardPanelImpl sourceCard;

    public void mousePressed(MouseEvent e) {
        sourceCard = (CardPanelImpl) e.getSource();

        // todo 打出这张牌
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);

        // 调整位置
        sourceCard = (CardPanelImpl) e.getSource();
        Point p = sourceCard.getLocation();
        p.y -= 20;
        sourceCard.setLocation(p);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // 调整位置
        sourceCard = (CardPanelImpl) e.getSource();
        Point point = sourceCard.getLocation();
        point.y += 20;
        sourceCard.setLocation(point);
    }

}
