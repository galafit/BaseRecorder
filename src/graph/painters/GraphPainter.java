package graph.painters;

import data.DataSet;
import graph.GraphType;

import java.awt.*;

public class GraphPainter {
    private Color graphColor = Color.YELLOW;
    private Color undefinedColor = new Color(0,150, 250);
    private GraphType graphType;

    public GraphPainter(GraphType graphType) {
        this.graphType = graphType;
    }

    public void setColor(Color graphColor) {
        this.graphColor = graphColor;
    }

    public void paint(Graphics g, double zoom, int startIndex, DataSet graph) {
        g.setColor(graphColor);
        if (graph != null && startIndex >= 0 && startIndex < graph.size()) {
            int width = g.getClipBounds().width;
            int height = g.getClipBounds().height;
            int endPoint = Math.min(width, (graph.size() - startIndex));
            int value = graph.get(startIndex);
            int y = (int) Math.round(zoom * value);
            VerticalLine vLine = new VerticalLine(y);
            for (int x = 0; x < endPoint; x++) {
                value = graph.get(x + startIndex);
                y = (int) Math.round(zoom * value);
                if(graphType == GraphType.PAPA) {
                    if(value == DataSet.FALSE) {
                        g.setColor(undefinedColor);
                        //drawVerticalLine(g, x, 0, vLine);
                        g.drawLine(x, 0, x, height);
                    }
                    else{
                        g.setColor(graphColor);
                        drawVerticalLine(g, x, y, vLine);
                    }
                }
                if(graphType == GraphType.LINE) {
                    int xPrevious = 0;
                    if(x + startIndex > 0) {
                       xPrevious = x - 1;
                    }
                    int valuePrevious = graph.get(xPrevious + startIndex);
                    int yPrevious = (int) Math.round(zoom * valuePrevious);
                    if(value == DataSet.FALSE) {
                        g.setColor(undefinedColor);
                        g.drawLine(x, 0, x, height);
                    }
                    else if(value == DataSet.TRUE) {

                    }
                    else{
                        g.setColor(graphColor);
                        g.drawLine(xPrevious, yPrevious, x, y);
                    }
                }
                if(graphType == GraphType.BAR) {
                    if(value == DataSet.FALSE) {
                        g.setColor(undefinedColor);
                        g.drawLine(x, 0, x, height);
                    }
                    else{
                        g.setColor(graphColor);
                        g.drawLine(x, 0, x, y);
                    }
                }

            }
        }
    }

    private void drawVerticalLine(Graphics g, int x, int y, VerticalLine vLine) {
        vLine.setNewBounds(y);
        g.drawLine(x, vLine.min, x, vLine.max);
    }

    class VerticalLine {
        int max = 0;
        int min = -1;

        VerticalLine(int y) {
            setNewBounds(y);
        }

        void setNewBounds(int y) {
            if (y >= min && y <= max) {
                min = max = y;
            } else if (y > max) {
                min = max + 1;
                max = y;
            } else if (y < min) {
                max = min - 1;
                min = y;
            }
        }
    }

}
