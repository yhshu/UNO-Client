package GUI.GameWindow;

import Service.GameService;
import Service.GameServiceImpl;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 玩家当前持有卡牌的鼠标监听器
 */
public class PlayerCardMouseAdapter extends MouseAdapter {
    private CardFrontPanel sourceCard;
    private GameService gameService = new GameServiceImpl();

    public void mousePressed(MouseEvent e) {
        sourceCard = (CardFrontPanel) e.getSource();
        // 打出这张牌

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);

        // 调整位置
        sourceCard = (CardFrontPanel) e.getSource();
        Point p = sourceCard.getLocation();
        p.y -= 20;
        sourceCard.setLocation(p);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // 调整位置
        sourceCard = (CardFrontPanel) e.getSource();
        Point point = sourceCard.getLocation();
        point.y += 20;
        sourceCard.setLocation(point);
    }
}
