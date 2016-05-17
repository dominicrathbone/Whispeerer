package whispeerer.whispeerer;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;

/**
 * Created by Dominic on 17/05/2016.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final Point dimensions;

    public MyGLSurfaceView(Context c, Point dimensions) {
        super(c);
        this.dimensions = dimensions;
    }

    @Override
    protected void onMeasure(int unusedX, int unusedY) {
        setMeasuredDimension(dimensions.x, dimensions.y);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSystemUiVisibility(SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
