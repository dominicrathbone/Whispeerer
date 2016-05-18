package whispeerer.whispeerer;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;

/**
 * Created by Dominic on 17/05/2016.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private Point dimensions;

    public MyGLSurfaceView(Context c, Point dimensions) {
        super(c);
        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.dimensions = dimensions;
    }

    @Override
    protected void onMeasure(int unusedX, int unusedY) {
        setMeasuredDimension(dimensions.x, dimensions.y);
    }

    public void updateDisplaySize(Point dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
