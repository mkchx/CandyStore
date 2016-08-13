package mkchx.pomelo.sampleapp.domain.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;

import mkchx.pomelo.sampleapp.domain.models.dto.AnimDto;

public class AnimationUtil {

    public static void animateAlpha(View view, float alpha) {
        ViewCompat.animate(view).alpha(alpha).setInterpolator(new DecelerateInterpolator()).start();
    }

    public static void revealEffect(AnimDto revealDto) {

        if (revealDto != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(revealDto.getView(), revealDto.getcX(), revealDto.getcY(), 0, revealDto.getRadius());
                anim.setInterpolator(new DecelerateInterpolator());
                anim.setDuration(700);

                // make the view visible and start the animation
                revealDto.getView().setVisibility(View.VISIBLE);

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        revealDto.executeRunnable();
                    }
                });

                anim.start();
            } else {

                revealDto.getView().setVisibility(View.VISIBLE);
                revealDto.executeRunnable();
            }
        }
    }

    public static void hideEffect(AnimDto hideDto) {

        if (hideDto != null) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                // create the animation (the final radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(hideDto.getView(), hideDto.getcX(), hideDto.getcY(), hideDto.getRadius(), 0);
                anim.setInterpolator(new DecelerateInterpolator());

                // make the view invisible when the animation is done
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        hideDto.getView().setVisibility(View.INVISIBLE);
                        hideDto.executeRunnable();
                    }
                });

                anim.start();
            } else {
                hideDto.getView().setVisibility(View.INVISIBLE);
                hideDto.executeRunnable();
            }
        }
    }
}
