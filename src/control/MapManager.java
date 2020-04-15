/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import business.facade.LocalFacade;
import helpers.Hexagon;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.TextAlignment;
import model.Jogador;
import model.Local;
import model.Personagem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import persistence.local.ListFactory;

/**
 *
 * @author jmoura
 */
public class MapManager {

    private static final Log log = LogFactory.getLog(MapManager.class);
    private static final LocalFacade localFacade = new LocalFacade();
    private Point farPoint;
    private int xHexes;
    private int yHexes;
    private final int hexSize;
    private final Double zoomFactor;

    public MapManager() {
        this.hexSize = 60;
        this.zoomFactor = 0.5d;
    }

    /**
     * for reference only. Cannibalize then delete
     *
     * @param listaLocal
     * @param listaPers
     * @param observer
     * @return
     */
    private BufferedImage printMapaGeral(Collection<Local> listaLocal, Collection<Personagem> listaPers, Jogador observer) {
        if (farPoint == null) {
            this.farPoint = getMapMaxSize(listaLocal);
        }

        //cria a imagem base
        BufferedImage megaMap = new BufferedImage(farPoint.x, farPoint.y, BufferedImage.TRANSLUCENT);
        final Graphics2D big = megaMap.createGraphics();

        //desenhando box para o mapa
        big.setBackground(Color.WHITE);
        big.clearRect(0, 0, farPoint.x, farPoint.y);
        big.setColor(Color.BLACK);
        big.setFont(new Font("Verdana", Font.PLAIN, 10));
        for (Local local : listaLocal) {
            //printHex(big, local, observer);
        }
        big.dispose(); //libera memoria
        return megaMap;
    }

    public Canvas getCanvas() {
        ListFactory listFactory = new ListFactory();
        Collection<Local> listaLocal = listFactory.listLocais().values();
        if (farPoint == null) {
            getMapMaxSize(listaLocal);
        }

        Canvas canvas = new Canvas((xHexes + 1) * hexSize * zoomFactor, (yHexes + 1) * hexSize * 3 / 4 * zoomFactor);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.scale(zoomFactor, zoomFactor);
        doRenderDeserts(gc, listaLocal);
        return canvas;
    }

    private void doRenderDeserts(GraphicsContext gc, Collection<Local> listaLocal) {
        Image desert = new Image("images/mapa/hex_2b_deserto.gif");
        //centering text
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        for (double x = 0; x < xHexes; x++) {
            for (double y = 0; y < yHexes; y++) {
                Point2D ret;
                if (y % 2 == 0) {
                    ret = new Point2D(x * hexSize, y * hexSize * 3 / 4);
                } else {
                    ret = new Point2D(x * hexSize + hexSize / 2, y * hexSize * 3 / 4);
                }

                gc.drawImage(desert, ret.getX(), ret.getY());
                Hexagon hex = new Hexagon(30d + ret.getX(), 30d + ret.getY());
                //System.out.println(ret.toString());
                // Set fill color
                gc.setFill(javafx.scene.paint.Color.rgb(188, 143, 143, 0.5));
                gc.setStroke(javafx.scene.paint.Color.RED);
//                gc.fillPolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);
                //gc.strokePolygon(hex.getListXCoord(), hex.getListYCoord(), hex.getListXCoord().length);
                // Set line width
                gc.setLineWidth(1.0);
                gc.setFill(javafx.scene.paint.Color.BLUE);
                // Draw a filled Text
                gc.fillText(
                        String.format("%s %s", (int) x + 1, (int) y + 1),
                        hex.getCenterPoint().getX(), hex.getCenterPoint().getY());
            }
        }
    }

    public Point getMapMaxSize(Collection<Local> listaLocal) {
        int[] ret = {0, 0};
        int row, col;
        for (Local local : listaLocal) {
            row = localFacade.getRow(local);
            col = localFacade.getCol(local);
            if (row > ret[0]) {
                ret[0] = row;
            }
            if (col > ret[1]) {
                ret[1] = col;
            }
        }
        this.farPoint = new Point(ret[1] * 60 + 30, ret[0] * 45 + 60);
        xHexes = ret[1];
        yHexes = ret[0];
        return this.farPoint;
    }
}
