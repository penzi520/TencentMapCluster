package map.penzi.cn.demo;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import map.penzi.cn.clustering.Cluster;
import map.penzi.cn.clustering.ClusterManager;
import map.penzi.cn.clustering.MultiDrawable;
import map.penzi.cn.clustering.ui.IconGenerator;
import map.penzi.cn.clustering.view.DefaultClusterRenderer;
import map.penzi.cn.demo.model.Person;

public class MainActivity extends AppCompatActivity implements
        ClusterManager.OnClusterClickListener<Person>,
        ClusterManager.OnClusterInfoWindowClickListener<Person>,
        ClusterManager.OnClusterItemClickListener<Person>,
        ClusterManager.OnClusterItemInfoWindowClickListener<Person> {

    private MapView mMapView;
    private TencentMap mTencentMap;
    private ClusterManager<Person> mClusterManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
    }

    private void initUi() {
        mMapView = (MapView) findViewById(R.id.mapView);
        mTencentMap = mMapView.getMap();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(22.53453,113.945743))
                .zoom(15)
                .build();
        mTencentMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //mTencentMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(22.53453,113.945743), 9.5f));

        mClusterManager = new ClusterManager<>(this, mTencentMap);
        mClusterManager.setRenderer(new PersonRenderer());
        mTencentMap.setOnCameraChangeListener(mClusterManager);
        mTencentMap.setOnMarkerClickListener(mClusterManager);
        mTencentMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        mClusterManager.cluster();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }


    @Override
    public boolean onClusterClick(Cluster<Person> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Person> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(Person item) {
        // Does nothing, but you could go into the user's profile page, for example.
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Person item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }

    private void addItems() {
        mClusterManager.addItem(new Person(new LatLng(22.53453,113.946743), "Title1", R.drawable.ic_launcher));
        mClusterManager.addItem(new Person(new LatLng(22.53453,113.949743), "Title2", R.drawable.ic_launcher));
        mClusterManager.addItem(new Person(new LatLng(22.53453,113.935743), "Title3", R.drawable.ic_launcher));
        mClusterManager.addItem(new Person(new LatLng(22.53453,113.925743), "Title4", R.drawable.ic_launcher));
        mClusterManager.addItem(new Person(new LatLng(22.53453,113.945843), "Title5", R.drawable.ic_launcher));
        mClusterManager.addItem(new Person(new LatLng(22.53453,113.915743), "Title6", R.drawable.ic_launcher));
        mClusterManager.addItem(new Person(new LatLng(22.53453,113.947743), "Title7", R.drawable.ic_launcher));
        mClusterManager.addItem(new Person(new LatLng(22.53453,113.975743), "Title8", R.drawable.ic_launcher));
        mClusterManager.addItem(new Person(new LatLng(22.53453,113.995743), "Title9", R.drawable.ic_launcher));
    }

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class PersonRenderer extends DefaultClusterRenderer<Person> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PersonRenderer() {
            super(getApplicationContext(), mTencentMap, mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(Person person, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageResource(person.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.name);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<Person> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).


            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Person p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));


        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }
}
