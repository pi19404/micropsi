/*
 * Created on 30.06.2005
 *
 */
package org.micropsi.comp.console.worldconsole;

import org.micropsi.common.coordinates.Position;

/**
 * @author Markus
 *
 */
public class ConnectionLine {
    private Position start;
    private Position end;
    
    public ConnectionLine(Position start, Position end) {
        this.start = start;
        this.end = end;
    }
    
    /**
     * @return Returns the end.
     */
    public Position getEnd() {
        return end;
    }
    
    /**
     * @return Returns the start.
     */
    public Position getStart() {
        return start;
    }
}
