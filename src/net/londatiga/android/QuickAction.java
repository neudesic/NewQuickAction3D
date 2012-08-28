package net.londatiga.android;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * QuickAction dialog, shows action list as icon and text like the one in Gallery3D app. Currently
 * supports vertical and horizontal layout.
 * 
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 * 
 *         Contributors: - Kevin Peck <kevinwpeck@gmail.com>
 */
public class QuickAction extends ContextualPopup {

    private List<ActionItem> actionItems = new ArrayList<ActionItem>();

    private int mChildPos;
	private int mInsertPos;
    private int mOrientation;
    private boolean showHorizontalSeperator = true;

    protected boolean mDidAction;
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

    private final Integer itemViewId;
    protected ViewGroup mTrack;

	/**
	 * Constructor for default vertical layout
	 * 
	 * @param context
	 *            Context
	 */
	public QuickAction(Context context) {
		this(context, VERTICAL);
	}

	/**
	 * Constructor allowing orientation override
	 * 
	 * @param context
	 *            Context
	 * @param orientation
	 *            Layout orientation, can be vartical or horizontal
	 */
	public QuickAction(Context context, int orientation) {
		this(context, orientation, null, null);
	}

    @Override
    protected void preShow() {
        super.preShow();

        mDidAction = false;
    }

    public QuickAction(Context context, int orientation, Integer rootViewId, Integer itemViewId) {
		super(context);

		mOrientation = orientation;
		this.itemViewId = itemViewId;

        if (rootViewId != null)
		{
			setRootViewId(rootViewId.intValue());
		}
		else
		{
			if (mOrientation == HORIZONTAL)
			{
				setRootViewId(R.layout.popup_horizontal);
			}
			else
			{
				setRootViewId(R.layout.popup_vertical);
			}
		}
		mAnimStyle = ANIM_AUTO;
		mChildPos = 0;
	}

	/**
	 * Get action item at an index
	 * 
	 * @param index
	 *            Index of item (position from callback)
	 * 
	 * @return Action Item at the position
	 */
	public ActionItem getActionItem(int index)
	{
		return actionItems.get(index);
	}

    /**
	 * Add action item
	 * 
	 * @param action
	 *            {@link ActionItem}
	 */
	public void addActionItem(final ActionItem action)
	{
		actionItems.add(action);

		String title = action.getTitle();
		Drawable icon = action.getIcon();

		View container;

		if (itemViewId != null)
		{
			container = mInflater.inflate(itemViewId.intValue(), null);
		}
		else
		{
			if (mOrientation == HORIZONTAL)
			{
				container = mInflater.inflate(R.layout.action_item_horizontal, null);
			}
			else
			{
				container = mInflater.inflate(R.layout.action_item_vertical, null);
			}
		}

		ImageView img = (ImageView) container.findViewById(R.id.iv_icon);
		TextView text = (TextView) container.findViewById(R.id.tv_title);

		if (icon != null)
		{
			img.setImageDrawable(icon);
		}
		else
		{
			img.setVisibility(View.GONE);
		}

		if (title != null)
		{
			text.setText(title);
		}
		else
		{
			text.setVisibility(View.GONE);
		}

		final int pos = mChildPos;
		//		final int actionId 	= action.getActionId();

		container.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				action.itemClicked(v);
				//				if (mItemClickListener != null) {
				//                    mItemClickListener.onItemClick(QuickAction.this, pos, actionId);
				//                }

				if (!getActionItem(pos).isSticky())
				{
					mDidAction = true;

					dismiss();
				}
			}
		});

		container.setFocusable(true);
		container.setClickable(true);

		if (showHorizontalSeperator &&  mOrientation == HORIZONTAL && mChildPos != 0)
		{
			View separator = mInflater.inflate(R.layout.horiz_separator, null);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);

			separator.setLayoutParams(params);
			separator.setPadding(5, 0, 5, 0);

			mTrack.addView(separator, mInsertPos);

			mInsertPos++;
		}

		mTrack.addView(container, mInsertPos);

		mChildPos++;
		mInsertPos++;
	}

    public boolean isShowHorizontalSeperator()
	{
		return showHorizontalSeperator;
	}

    @Override
    public void setRootViewId(int id) {

        super.setRootViewId(id);

        mTrack = (ViewGroup) mRootView.findViewById(R.id.tracks);
    }

    public void setShowHorizontalSeperator(boolean showHorizontalSeperator)
	{
		this.showHorizontalSeperator = showHorizontalSeperator;
	}

	/**
	 * Listener for item click
	 * 
	 */
	public interface OnActionItemClickListener
	{
		public abstract void onItemClick(View v);
	}

}