package ru.ptrff.tracktag.worker;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.ptrff.tracktag.R;
import ru.ptrff.tracktag.api.MapsRepository;
import ru.ptrff.tracktag.data.UserData;
import ru.ptrff.tracktag.models.Tag;
import ru.ptrff.tracktag.models.User;

public class PostCheckingWorker extends Worker {
    private UserData data;
    private MapsRepository repo;

    public PostCheckingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        data = UserData.getInstance();
        repo = new MapsRepository();
    }

    @NonNull
    @Override
    public Result doWork() {
        checkForNewPosts();
        return Result.success();
    }

    @SuppressLint("CheckResult")
    private void checkForNewPosts() {
        repo
                .getAllTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleNewPosts, this::handleError);
    }

    private void handleNewPosts(List<Tag> tags) {
        Collections.reverse(tags);
        for (User sub : data.getSubs()) {
            Log.d(getClass().getCanonicalName(), "checking for new posts for " + sub.getUsername());
            for (Tag tag : tags) {
                if (tag.getUser() != null && tag.getUser().getUsername().equals(sub.getUsername())) {
                    if (!tag.getId().equals(data.getLastTagId(sub))) {
                        createNotification(
                                getApplicationContext().getString(R.string.something_new),
                                getApplicationContext().getString(R.string.user_posted_a_new_tag, sub.getUsername()),
                                sub
                        );
                        Log.d(getClass().getCanonicalName(), "new post " + tag.getId() + " saved id " + data.getLastTagId(sub));
                    } else {
                        createNotification("no for", sub.getUsername(), sub);
                        Log.d(getClass().getCanonicalName(), "no new post");
                    }
                    break;
                }
            }
        }
    }

    private void createNotification(String title, String content, User user) {
        {
            // creating channel for sub (will be ignored if channel id already exists)
            CharSequence name = "New tags from "+user.getUsername();
            String description = "Receive updates about new tags from "+user.getUsername();
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("tracktag-"+user.getId(), name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // creating notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "tracktag")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // displaying notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        // unique id for each notification
        notificationManager.notify(0, builder.build());
    }

    private void handleError(Throwable throwable) {
        Log.e(getClass().getCanonicalName(), "Error while checking for new posts", throwable);
    }
}
