package mkchx.pomelo.sampleapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mkchx.pomelo.sampleapp.models.dto.FoursquareDto;
import mkchx.pomelo.sampleapp.views.activity.MainActivity;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(AndroidJUnit4.class)
public class RetroRequestsTest {

    private Context mContext;
    private MainActivity mActivity;


    @Rule
    public final ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void initTargetContext() {
        mContext = InstrumentationRegistry.getTargetContext();
        mActivity = activityTestRule.getActivity();
    }

    @Before
    public void testPreconditions() {
        assertThat(mActivity, instanceOf(MainActivity.class));
        assertThat(mContext, notNullValue());
    }

    @Test
    public void testSearchApi() {

        Map<String, String> params = new HashMap<>();
        params.put("ll", "40.765001,-73.978921");
        params.put("categoryId", "4d4b7105d754a06372d81259");
        params.put("limit", "5");
        params.put("venuePhotos", "1");
        params.put("client_id", mContext.getString(R.string.fsq_client_id));
        params.put("client_secret", mContext.getString(R.string.fsq_client_secret));
        params.put("v", "20160722");

        Response<FoursquareDto> obj = null;
        try {
            obj = mActivity.getApiWrapper().getTestSearch(params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(obj.body(), notNullValue());
    }

    @Test
    public void testPhotoApi() {

        Map<String, String> params = new HashMap<>();
        params.put("limit", "1");
        params.put("client_id", mContext.getString(R.string.fsq_client_id));
        params.put("client_secret", mContext.getString(R.string.fsq_client_secret));
        params.put("v", "20160723");

        Response<FoursquareDto> obj = null;
        try {
            obj = mActivity.getApiWrapper().getTestPhoto("4e65e97e1fc747ca49d94c00", params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat(obj.body(), notNullValue());
    }
}