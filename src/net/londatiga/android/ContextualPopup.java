package net.londatiga.android;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;

/**
 * Created with IntelliJ IDEA.
 * User: BenMonro
 * Date: 8/27/12
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContextualPopup extends PopupWindows implements PopupWindow.OnDismissListener {
    public static final int ANIM_GROW_FROM_LEFT = 1;
    public static final int ANIM_GROW_FROM_RIGHT = 2;
    public static final int ANIM_GROW_FROM_CENTER = 3;
    public static final int ANIM_REFLECT = 4;
    public static final int ANIM_AUTO = 5;
    protected View mRootView;
    private ImageView mArrowUp;
    private ImageView mArrowDown;
    protected LayoutInflater mInflater;

    private ScrollView mScroller;
    protected OnDismissListener mDismissListener;
    protected boolean mDidAction;
    protected int mAnimStyle;
    private int rootWidth = 0;
    private RenderingMode renderingMode = RenderingMode.VERTICAL;
    private int rootHeight = 0;
    private int screenHeight = 0;
    private int screenWidth = 0;
    private ImageView mArrowLeft;
    private ImageView mArrowRight;

    public RenderingMode getRenderingMode() {
        return renderingMode;
    }

    public void setRenderingMode(RenderingMode renderingMode) {
        this.renderingMode = renderingMode;
    }


    public enum RenderingMode {
        VERTICAL,
        HORIZONTAL
    }

    public ContextualPopup(Context context) {
        super(context);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


//        setRootViewId(R.layout.popup_horizontal);

        mAnimStyle = ANIM_AUTO;
    }

    /**
     * Set root view.
     *
     * @param id Layout resource id
     */
    public void setRootViewId(int id) {
        mRootView = (ViewGroup) mInflater.inflate(id, null);

        mArrowDown = (ImageView) mRootView.findViewById(R.id.arrow_down);
        mArrowUp = (ImageView) mRootView.findViewById(R.id.arrow_up);
        mArrowLeft = (ImageView) mRootView.findViewById(R.id.arrow_left);
        mArrowRight = (ImageView) mRootView.findViewById(R.id.arrow_right);

        mScroller = (ScrollView) mRootView.findViewById(R.id.scroller);

        //This was previously defined on show() method, moved here to prevent force close that occured
        //when tapping fastly on a view to show quickaction dialog.
        //Thanx to zammbi (github.com/zammbi)
        mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setContentView(mRootView);
    }

    /**
     * Set animation style
     *
     * @param mAnimStyle animation style, default is set to ANIM_AUTO
     */
    public void setAnimStyle(int mAnimStyle) {
        this.mAnimStyle = mAnimStyle;
    }

    /**
     * Show quickaction popup. Popup is automatically positioned, on top or bottom of anchor view.
     */
    public void show(View anchor) {
        preShow();

        mDidAction = false;


        mRootView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        rootHeight = mRootView.getMeasuredHeight();

        if (rootWidth == 0) {
            rootWidth = mRootView.getMeasuredWidth();
        }

        screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        screenHeight = mWindowManager.getDefaultDisplay().getHeight();


        if (renderingMode == RenderingMode.VERTICAL) {
            showInVerticalMode(anchor);
        } else {
            showInHorizontalMode(anchor);
        }
    }

    private void showInHorizontalMode(View anchor) {
        int xPos, yPos, arrowPos;

        Rect anchorRect = getRectForAnchor(anchor);


        //automatically get X coord of popup (top left)
        if ((anchorRect.top + rootHeight) > screenHeight) {
            yPos = anchorRect.top - (rootHeight - anchor.getHeight());
            yPos = (yPos < 0) ? 0 : yPos;

            arrowPos = anchorRect.centerY() - yPos;

        } else {
            if (anchor.getHeight() > rootHeight) {
                yPos = anchorRect.centerY() - (rootHeight / 2);
            } else {
                yPos = anchorRect.top;
            }

            arrowPos = anchorRect.centerY() - yPos;
        }

        int dyLeft = anchorRect.left;
        int dyRight = screenWidth - anchorRect.right;

        boolean onLeft = (dyLeft > dyRight) ? true : false;

        if (onLeft) {
            if (rootWidth > dyLeft) {
                xPos = 15;
                ViewGroup.LayoutParams l = mScroller.getLayoutParams();
                l.width = dyLeft - anchor.getWidth();
            } else {
                xPos = anchorRect.left - rootWidth;
            }
        } else {
            xPos = anchorRect.right;

            if (rootWidth > dyRight) {
                ViewGroup.LayoutParams l = mScroller.getLayoutParams();
                l.width = dyRight;
            }
        }

        showHorizontalArrow(((onLeft) ? R.id.arrow_left : R.id.arrow_right), arrowPos);

//        setAnimationStyle(screenWidth, anchorRect.centerX(), onLeft);

        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }


    private void showInVerticalMode(View anchor) {
        int xPos, yPos, arrowPos;


        Rect anchorRect = getRectForAnchor(anchor);

        //automatically get X coord of popup (top left)
        if ((anchorRect.left + rootWidth) > screenWidth) {
            xPos = anchorRect.left - (rootWidth - anchor.getWidth());
            xPos = (xPos < 0) ? 0 : xPos;

            arrowPos = anchorRect.centerX() - xPos;

        } else {
            if (anchor.getWidth() > rootWidth) {
                xPos = anchorRect.centerX() - (rootWidth / 2);
            } else {
                xPos = anchorRect.left;
            }

            arrowPos = anchorRect.centerX() - xPos;
        }

        int dyTop = anchorRect.top;
        int dyBottom = screenHeight - anchorRect.bottom;

        boolean onTop = (dyTop > dyBottom) ? true : false;

        if (onTop) {
            if (rootHeight > dyTop) {
                yPos = 15;
                ViewGroup.LayoutParams l = mScroller.getLayoutParams();
                l.height = dyTop - anchor.getHeight();
            } else {
                yPos = anchorRect.top - rootHeight;
            }
        } else {
            yPos = anchorRect.bottom;

            if (rootHeight > dyBottom) {
                ViewGroup.LayoutParams l = mScroller.getLayoutParams();
                l.height = dyBottom;
            }
        }

        showVerticalArrow(((onTop) ? R.id.arrow_down : R.id.arrow_up), arrowPos);

        setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);

        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    private Rect getRectForAnchor(View anchor) {
        int[] location = new int[2];

        anchor.getLocationOnScreen(location);

        return new Rect(location[0], location[1], location[0] + anchor.getWidth(), location[1] + anchor.getHeight());
    }

    /**
     * Set animation style
     *
     * @param screenWidth screen width
     * @param requestedX  distance from left edge
     * @param onTop       flag to indicate where the popup should be displayed. Set TRUE if displayed on top
     *                    of anchor view and vice versa
     */
    private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
        int arrowPos = requestedX - mArrowUp.getMeasuredWidth() / 2;

        switch (mAnimStyle) {
            case ANIM_GROW_FROM_LEFT:
                mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
                break;

            case ANIM_GROW_FROM_RIGHT:
                mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
                break;

            case ANIM_GROW_FROM_CENTER:
                mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
                break;

            case ANIM_REFLECT:
                mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect : R.style.Animations_PopDownMenu_Reflect);
                break;

            case ANIM_AUTO:
                if (arrowPos <= screenWidth / 4) {
                    mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left : R.style.Animations_PopDownMenu_Left);
                } else if (arrowPos > screenWidth / 4 && arrowPos < 3 * (screenWidth / 4)) {
                    mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
                } else {
                    mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right : R.style.Animations_PopDownMenu_Right);
                }

                break;
        }
    }

    /**
     * Show horizontal arrow
     *
     * @param whichArrow arrow type resource id
     * @param requestedY distance from top screen
     */
    private void showHorizontalArrow(int whichArrow, int requestedY) {
        final View showArrow = (whichArrow == R.id.arrow_right) ? mArrowLeft : mArrowRight;
        final View hideArrow = (whichArrow == R.id.arrow_right) ? mArrowRight: mArrowLeft;

        final int arrowHeight = mArrowLeft.getMeasuredHeight();

        showArrow.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow.getLayoutParams();

        param.topMargin = requestedY - arrowHeight / 2;

        hideArrow.setVisibility(View.INVISIBLE);
    }

    /**
     * Show arrow
     *
     * @param whichArrow arrow type resource id
     * @param requestedX distance from left screen
     */
    private void showVerticalArrow(int whichArrow, int requestedX) {
        final View showArrow = (whichArrow == R.id.arrow_up) ? mArrowUp : mArrowDown;
        final View hideArrow = (whichArrow == R.id.arrow_up) ? mArrowDown : mArrowUp;

        final int arrowWidth = mArrowUp.getMeasuredWidth();

        showArrow.setVisibility(View.VISIBLE);

        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) showArrow.getLayoutParams();

        param.leftMargin = requestedX - arrowWidth / 2;

        hideArrow.setVisibility(View.INVISIBLE);
    }

    /**
     * Set listener for window dismissed. This listener will only be fired if the quicakction dialog
     * is dismissed by clicking outside the dialog or clicking on sticky item.
     */
    public void setOnDismissListener(ContextualPopup.OnDismissListener listener) {
        setOnDismissListener(this);

        mDismissListener = listener;
    }

    @Override
    public void onDismiss() {
        if (!mDidAction && mDismissListener != null) {
            mDismissListener.onDismiss();
        }
    }

    /**
     * Listener for window dismiss
     */
    public interface OnDismissListener {
        public abstract void onDismiss();
    }
}
