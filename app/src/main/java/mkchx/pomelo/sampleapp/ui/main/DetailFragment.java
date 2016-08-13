package mkchx.pomelo.sampleapp.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mkchx.pomelo.sampleapp.ConstKey;
import mkchx.pomelo.sampleapp.R;
import mkchx.pomelo.sampleapp.domain.interfaces.MainInterface;
import mkchx.pomelo.sampleapp.domain.models.api.Items;
import mkchx.pomelo.sampleapp.domain.models.api.Photos;
import mkchx.pomelo.sampleapp.domain.models.api.Venues;
import mkchx.pomelo.sampleapp.domain.utils.DebugUtil;
import mkchx.pomelo.sampleapp.domain.utils.RoundedUtil;

public class DetailFragment extends Fragment {

    MainInterface mainInterface;

    Unbinder mUnbinder;

    Venues mVenues;
    Photos mPhotos;

    @BindView(R.id.venue_image)
    ImageView uiVenueImage;

    @BindView(R.id.venue_stats)
    TextView uiVenueStats;

    @BindView(R.id.venue_title)
    TextView uiVenueTitle;

    @BindView(R.id.venue_address)
    TextView uiVenueAddress;

    @BindView(R.id.venue_category)
    TextView uiVenueCategory;

    @BindView(R.id.detail_container)
    LinearLayout uiDetailContainer;

    @BindView(R.id.loading_spinner)
    ProgressBar uiSpinnerLayout;

    public DetailFragment newInstance(Bundle args) {
        DetailFragment myFragment = new DetailFragment();
        myFragment.setArguments(args);

        return myFragment;
    }

    public void destroy() {

        DebugUtil.output("DetailFragment", "DetailFragment destroy()");

        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }

        mVenues = null;
        mPhotos = null;

        if (uiVenueImage != null) {
            uiVenueImage.setImageDrawable(null);
            uiVenueImage = null;
        }

        uiDetailContainer = null;
        uiVenueStats = null;
        uiVenueTitle = null;
        uiVenueAddress = null;
        uiVenueCategory = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            if (context instanceof MainInterface) {
                mainInterface = (MainInterface) context;
            }

        } catch (ClassCastException e) {
            throw new IllegalStateException(context.getClass()
                    .getSimpleName()
                    + " does not implement contract interface for "
                    + getClass().getSimpleName(), e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainInterface = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Restore saved state
        if (savedInstanceState == null) {

            Bundle args = getArguments();

            if (args != null) {

                if (args.containsKey(ConstKey.KEY_VENUE)) {
                    mVenues = args.getParcelable(ConstKey.KEY_VENUE);
                }

                if (args.containsKey(ConstKey.KEY_PHOTO)) {
                    mPhotos = args.getParcelable(ConstKey.KEY_PHOTO);
                }
            }
        }

        onEverythingDone();
    }

    public void updateDetailView(Venues venues, Photos photos) {

        if (uiDetailContainer != null && uiSpinnerLayout != null) {
            uiDetailContainer.setVisibility(View.GONE);
            uiSpinnerLayout.setVisibility(View.VISIBLE);
        }

        mVenues = venues;
        mPhotos = photos;

        onEverythingDone();
    }

    public void onEverythingDone() {

        if (mVenues != null) {

            if (mVenues.getStats() != null) {
                uiVenueStats.setText(String.valueOf(mVenues.getStats().getCheckinsCount()));
            } else {
                uiVenueStats.setVisibility(View.GONE);
            }

            if (mVenues.getLocation() != null) {
                uiVenueAddress.setText(mVenues.getLocation().getAddress());
            } else {
                uiVenueAddress.setVisibility(View.GONE);
            }

            if (mVenues.getCategories() != null && mVenues.getCategories().size() > 0) {
                uiVenueCategory.setText(mVenues.getCategories().get(0).getName());
            } else {
                uiVenueCategory.setVisibility(View.GONE);
            }

            uiVenueTitle.setText(mVenues.getName());
        }

        if (mPhotos != null && mPhotos.getCount() > 0) {

            uiVenueImage.setImageDrawable(null);

            Items item = mPhotos.getItems().get(0);

            String url = item.getPrefix() + "640x480" + item.getSuffix();

            // TODO: image quality based on device density
            Picasso.with(getContext()).load(url)
                    .placeholder(R.color.colorEmpty)
                    .transform(new RoundedUtil(30, RoundedUtil.Corners.ALL))
                    //.resize(mainInterface.getDeviceConfig().getWidthContent(), getResources().getDimensionPixelSize(R.dimen.venue_photo_height))
                    .fit()
                    .into(uiVenueImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            // displayContent(true); TODO: for some reason success never gets called, neither failure
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(getContext(), R.string.api_failure, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // displayContent(true);
        }

        displayContent(true);
    }

    private void displayContent(boolean show) {

        if (uiDetailContainer != null && uiSpinnerLayout != null) {
            uiDetailContainer.setVisibility((show ? View.VISIBLE : View.GONE));
            uiSpinnerLayout.setVisibility((show ? View.GONE : View.VISIBLE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mContentView = inflater.inflate(R.layout.fragment_detail, container, false);
        mUnbinder = ButterKnife.bind(this, mContentView);

        return mContentView;
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = AppStart.getApp().getRefWatcher(getActivity());
        refWatcher.watch(this);
    }*/
}
