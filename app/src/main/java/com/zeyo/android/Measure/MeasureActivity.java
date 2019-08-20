package com.zeyo.android.Measure;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.examples.java.helloar.CameraPermissionHelper;
import com.google.ar.core.examples.java.helloar.DisplayRotationHelper;
import com.google.ar.core.examples.java.helloar.rendering.BackgroundRenderer;
import com.google.ar.core.examples.java.helloar.rendering.ObjectRenderer;
import com.google.ar.core.examples.java.helloar.rendering.PlaneRenderer;
import com.google.ar.core.examples.java.helloar.rendering.PointCloudRenderer;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.NotTrackingException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.zeyo.android.R;
import com.zeyo.android.renderer.RectanglePolygonRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import io.fabric.sdk.android.Fabric;
/*
측정을 하는 클래스. StartMeasure클래스에서 측정 항목이 넘어오면 해당 항목당 측정을 해서 결과값을 MeasureResult로 넘겨준다.
 */
public class MeasureActivity extends AppCompatActivity {
    public static Context mContext;
    private final int DEFAULT_VALUE = -1;
    private int nowTouchingPointIndex = DEFAULT_VALUE;
    private static final String TAG = MeasureActivity.class.getSimpleName();
    private GLSurfaceView surfaceView = null;
    ImageView center_image;
    private boolean installRequested;
    private Session session = null;
    private GestureDetector gestureDetector;
    private Snackbar messageSnackbar = null;
    private DisplayRotationHelper displayRotationHelper;
    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private final PlaneRenderer planeRenderer = new PlaneRenderer();
    private final PointCloudRenderer pointCloud = new PointCloudRenderer();
    private final ObjectRenderer cube = new ObjectRenderer();
    private final ObjectRenderer cubeSelected = new ObjectRenderer();
    private RectanglePolygonRenderer rectRenderer = null;
    int count = 0;                                            //측정 항목에 따라 변하는 카운트 변수
    int temp_flag = 0;                                        //최초 쓰레드 실행을 판단하는 flag 변수
    private final float[] anchorMatrix = new float[16];
    private float x,y;
    private int t_flag = 0;                                   //꾹 누르는 이벤트를 판단하는 flag 변수
    int result[] = new int[10];                           //각 항목마다 측정결과가 저장되는 result 배열, 크기 임의로 설정
    private ArrayBlockingQueue<MotionEvent> queuedSingleTaps = new ArrayBlockingQueue<>(16);
    private ArrayBlockingQueue<MotionEvent> queuedLongPress = new ArrayBlockingQueue<>(16);
    private ArrayList<Anchor> anchors = new ArrayList<>();    //찍는 점 담는 리스트
    private ArrayList[] temp_anchors;                         //이전에 찍은 점(모든 점)을 저장하는 리스트
    private ArrayBlockingQueue<Float> queuedScrollDx = new ArrayBlockingQueue<>(100);
    private ArrayBlockingQueue<Float> queuedScrollDy = new ArrayBlockingQueue<>(100);

    private int Cap_flag;                                      //현재 점 찍기 버튼을 눌렀는지 판단하는 flag
    private GLSurfaceRenderer glSerfaceRenderer = null;
    private GestureDetector.SimpleOnGestureListener gestureDetectorListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {           //화면 위 터치 이벤트 인지 -> 현재 액티비티에서 사용되지 않으나, 추후 추가될 가능성이 있어서 넣어놓았다.
            queuedSingleTaps.offer(e);                          //singleTap이벤트를 인지하며, 드래그 이벤트는 인지하지 않는다.
            return true;
        }
        @Override
        public void onLongPress(MotionEvent e) {                //화면에 특정 픽셀을 꾹 누르는 이벤트를 처리한다.
            queuedLongPress.offer(e);
        }
        @Override
        public boolean onDown(MotionEvent e) {                  //화면에서 손을 떼는 이벤트 처리
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,             //화면 위를 스크롤하는 이벤트를 처리한다. 현재 액티비티에서 사용하지 않는다 -> 드래그 이벤트를 따로 밑에서 처리한다.
                                float distanceX, float distanceY) {
            queuedScrollDx.offer(distanceX);
            queuedScrollDy.offer(distanceY);
            return true;
        }
    };
    private int item_size;
    ImageButton btn_fab;
    ImageButton btn_next;
    ImageButton btn_delete;
    ImageButton btn_prev;
    ImageButton btn_question;
    private TextView distance;
    TextView txt_delete, txt_prev, txt_fab, txt_next, txt_guide;
    FragmentPagerAdapter adapterViewPager;
    Button btn_end;
    Button guide_end_button;
    FrameLayout frame;
    LinearLayout guide_linear;
    LinearLayout measure_guide;
    LinearLayout guide_end;
    FrameLayout bottom;
    TextView measure_view;
    ArrayList<String> Measure_item;
    ArrayList<String> Image_item;
    ViewPager vpPager;
    SharedPreferences pref;          //다시 보지 않기 관련 이벤트 처리
    Boolean check;                   //다시 보지 않기 관련 이벤트 처리
    pl.droidsonroids.gif.GifImageView gifImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        distance = findViewById(R.id.distance);
        Drawable alpha2 = distance.getBackground();
        alpha2.setAlpha(90);
        btn_end =  (Button)findViewById(R.id.end);
        frame = (FrameLayout)findViewById(R.id.frame);
        bottom = findViewById(R.id.final_bottom);
        gifImageView = findViewById(R.id.myCoordinatorLayout);
        txt_delete = findViewById(R.id.txt_delete);
        txt_prev = findViewById(R.id.txt_prev);
        txt_fab = findViewById(R.id.txt_fab);
        txt_next = findViewById(R.id.txt_next);
        txt_guide = findViewById(R.id.txt_guide);
        frame.removeViewInLayout(gifImageView);
        btn_question = findViewById(R.id.question);
        btn_fab = findViewById(R.id.fab);
        btn_next = findViewById(R.id.next);
        btn_delete = findViewById(R.id.delete);
        btn_prev = findViewById(R.id.prev);
        center_image = findViewById(R.id.cameraFocusQR);
        measure_guide = findViewById(R.id.measure_guide);
        frame.removeViewInLayout(measure_guide);
        guide_linear = findViewById(R.id.guide_linear);
        vpPager = (ViewPager) findViewById(R.id.in_vpPager);
        guide_end = findViewById(R.id.guide_end);
        guide_end_button = findViewById(R.id.guide_end_button);
        frame.removeViewInLayout(guide_end);
        frame.removeView(guide_linear);
        frame.removeView(vpPager);
        mContext = this;
        Measure_item = ((StartMeasure)StartMeasure.mContext).Measure_item;
        Image_item = ((StartMeasure)StartMeasure.mContext).Image_item;
        item_size = Measure_item.size();
        if(temp_flag == 0){
            temp_anchors = new ArrayList[item_size];
            temp_flag = 1;
        }
        measure_view = findViewById(R.id.measure_item);
        measure_view.setText(Measure_item.get(count));
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        check = pref.getBoolean("guide",false);
        if(check){
            showLoadingMessage();
        }
        btn_question.setOnClickListener(new View.OnClickListener() {            //왼쪽 상단 물음표 버튼 클릭 이벤트 리스너
            public void onClick(View arg0) {
                frame.removeViewInLayout(measure_guide);                        //최상위 framelayout에서 child제거하고 add
                frame.addView(measure_guide);
                frame.removeViewInLayout(guide_end);
                frame.addView(guide_end);
                ImageView measure_image = findViewById(R.id.measure_image);
                Glide.with(mContext).load(Image_item.get(count)).into(measure_image);
                guide_end_button.setBackgroundResource(R.drawable.ic_x);
            }
        });
        btn_end.setOnClickListener(new View.OnClickListener() {                 //측정 가이드 버튼 클릭시 btn_end활성화
            public void onClick(View arg0) {                                    //btn_end 클릭시 측정 가이드 remove
                frame.removeView(vpPager);
                frame.removeView(guide_linear);
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener(){         //다음 버튼 누를시 작동하는 이벤트 리스너
            @Override
            public void onClick(View v) {

                if(count == item_size - 1){                             //모든 측정을 완료하고 다음 버튼 누를시 MeasureResult로 전환
                    count = 0;
                    Intent intent = new Intent(MeasureActivity.this, com.zeyo.android.Measure.MeasureResultActivity.class);
                    intent.putExtra("text", result);              //측정 결과 전송
                    MeasureActivity.this.finish();
                    startActivity(intent);
                }
                else if(count < item_size - 1  && anchors.size() == 2) {
                    if(temp_anchors[count+1] == null) {                   //다음 버튼을 눌렀을 때 측정이 안되어있는 화면일 경우
                        temp_anchors[count] = new ArrayList<Anchor>();
                        temp_anchors[count] = anchors;
                        anchors = new ArrayList<>();
                        count++;

                    }
                    else{
                        count++;
                        anchors = temp_anchors[count];                      //이미 측정이 되어있는 화면으로 이동할 경우
                        //distance.setText(Float.toString(result[count]));
                        //measure_view.setText(Measure_item.get(count));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            distance.setText("\t\t시작점을 찍어주세요.\t\t");
                            measure_view.setText(Measure_item.get(count));   //측정 결과 출력 텍스트 뷰
                        }
                    });
                }
            }
        });
        ImageButton guide = (ImageButton) findViewById(R.id.guide);
        guide.setOnClickListener(new View.OnClickListener() {                   //측정 가이드 버튼 클릭 이벤트 리스너
            public void onClick(View arg0) {
                frame.addView(vpPager);
                frame.addView(guide_linear);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                adapterViewPager = new InnerGuideViewPager.MyPagerAdapter(getSupportFragmentManager());
                vpPager.setAdapter(adapterViewPager);
                vpPager.setBackgroundColor(0x7F000000);
                ImageView indicator = findViewById(R.id.inner_circle_indicator);
                indicator.setBackgroundResource(R.drawable.indi1);
                vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    public void onPageScrollStateChanged(int state) {}
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                    public void onPageSelected(int position) {                                  //화면 전환 이벤트 처리
                        if(position == 0){                                                         //첫 번째 화면일 때
                            indicator.setBackgroundResource(R.drawable.indi1);
                        }
                        else if (position == 1){                                                                       //두 번째 화면일 때
                            indicator.setBackgroundResource(R.drawable.indi_two);
                        }
                        else{
                            indicator.setBackgroundResource(R.drawable.indi_third);
                        }
                    }
                });
            }
        });
        guide_end_button.setOnClickListener(new View.OnClickListener() {        //측정 가이드 종료 버튼 클릭 이벤트 리스너
            public void onClick(View arg0) {
                frame.removeView(measure_guide);
                frame.removeView(guide_end);
                guide_end_button.setBackgroundColor(0x00000000);
            }
        });
        btn_fab.setOnClickListener(new View.OnClickListener(){                   //점 찍기 버튼 클릭 이벤트 리스너
            @Override
            public void onClick(View v) {
                if(count < item_size) {
                    Cap_flag = 1;                                                //Cap_flag 1로 활성화
                }
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(anchors.size() > 0) {
                    anchors.remove(anchors.size() - 1);                 //가장 최근 찍은 점 삭제
                    nowTouchingPointIndex--;
                    if(anchors.size() == 0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                distance.setText("\t\t시작점을 찍어주세요.\t\t");
                            }
                        });
                    }
                }
            }
        });
        btn_prev.setOnClickListener(new View.OnClickListener(){                 //이전에 찍은 점 호출
            @Override
            public void onClick(View v) {
                if(count > 0) {
                    count--;                                                    //이전 버튼 클릭시 count--
                    anchors = temp_anchors[count];
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            distance.setText("\t\t" + result[count] + "cm\t\t");
                            measure_view.setText(Measure_item.get(count));
                        }
                    });
                }
            }
        });
        displayRotationHelper = new DisplayRotationHelper(/*context=*/ this);

        if(CameraPermissionHelper.hasCameraPermission(this)){
            setupRenderer();
        }
        installRequested = false;
    }

    private void setupRenderer(){
        if(surfaceView != null){
            return;
        }
        surfaceView = findViewById(R.id.surfaceview);

        gestureDetector = new GestureDetector(this, gestureDetectorListener);
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {                                 //Touch event 감지
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:                                               //Touch 감지
                        x=event.getX();
                        y=event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:                                               //Drag 감지
                        x=event.getX();
                        y=event.getY();
                        break;
                    case MotionEvent.ACTION_UP:                                                 //화면에서 손을 떼는 것 감지
                        t_flag = 0;
                        break;
                }
                return gestureDetector.onTouchEvent(event);
            }
        });
        glSerfaceRenderer = new GLSurfaceRenderer(this);
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        surfaceView.setRenderer(glSerfaceRenderer);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }

                session = new Session(/* context= */ this);
            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                message = "ARcore를 설치해주세요.";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "ARcore 업데이트가 필요합니다.";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "ZEYO 업데이트가 필요합니다.";
                exception = e;
            } catch (Exception e) {
                message = "AR을 지원하지 않는 기기입니다.";
                exception = e;
            }
            if (message != null) {
                showSnackbarMessage(message, true);
                Log.e(TAG, "Exception creating session", exception);
                return;
            }

            Config config = new Config(session);
            if (!session.isSupported(config)) {
                showSnackbarMessage("AR을 지원하지 않는 기기입니다.", true);
            }
            session.configure(config);
            setupRenderer();
        }
        try {
            session.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }
        surfaceView.onResume();
        displayRotationHelper.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (session != null) {
            displayRotationHelper.onPause();
            surfaceView.onPause();
            session.pause();
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    //위의 메소드들 모두 고려 안해도 되는 함수들 -> 실시간 렌더링 쓰레드 관련 함수
    //처음 나오는 애니메이션 화면. 평면이 인식되면 자동으로 애니메이션이 종료되며 점을 찍을 수 있다.
    public void showLoadingMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gifImageView.setBackgroundResource(R.drawable.mobile_moving);
                final View viewPos = findViewById(R.id.myCoordinatorLayout2);
                messageSnackbar = Snackbar.make(viewPos,
                        "측정을 시작하려면 스마트폰을\n좌우로 기울여주세요", Snackbar.LENGTH_INDEFINITE);
                View snakBarView = messageSnackbar.getView();
                snakBarView.setBackgroundColor(0x000000);
                TextView text = (TextView) snakBarView.findViewById(R.id.snackbar_text);
                text.setTextColor(Color.WHITE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    text.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                text.setTextSize(16);
                messageSnackbar.show();
            }
        });
    }
    //처음 나오는 애니메이션이 사라지는 함수. 평면이 인식되면 호출된다.
    private void hideLoadingMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(messageSnackbar != null) {
                    messageSnackbar.dismiss();
                }
                center_image.setBackgroundResource(R.drawable.center);
                frame.setBackgroundColor(0x000000);
                gifImageView.setBackgroundColor(0x000000);
                messageSnackbar = null;
            }
        });
    }
    //처음 나오는 애니메이션의 밑에 나오는 텍스트 문구. hideLoadingMessage 호출 시 사라진다.
    private void showSnackbarMessage(String message, boolean finishOnDismiss) {
        messageSnackbar =
                Snackbar.make(
                        MeasureActivity.this.findViewById(android.R.id.content),
                        message,
                        Snackbar.LENGTH_INDEFINITE);
        messageSnackbar.getView().setBackgroundColor(0xbf323232);
        if (finishOnDismiss) {
            messageSnackbar.setAction(
                    "Dismiss",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            messageSnackbar.dismiss();
                        }
                    });
            messageSnackbar.addCallback(
                    new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            finish();
                        }
                    });
        }
        messageSnackbar.show();
    }


    private class GLSurfaceRenderer implements GLSurfaceView.Renderer{
        private Context context;
        private int viewWidth = 0;
        private int viewHeight = 0;
        private final float cubeHitAreaRadius = 0.08f;
        private final float[] centerVertexOfCube = {0f, 0f, 0f, 1};
        private final float[] vertexResult = new float[4];

        private float[] tempTranslation = new float[3];
        private float[] tempRotation = new float[4];
        private float[] projmtx = new float[16];
        private float[] viewmtx = new float[16];

        public GLSurfaceRenderer(Context context){
            this.context = context;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
            backgroundRenderer.createOnGlThread(context);
            if (session != null) {
                session.setCameraTextureName(backgroundRenderer.getTextureId());
            }
            rectRenderer = new RectanglePolygonRenderer();
            try {
                cube.createOnGlThread(context, "dot.obj");                        //object 생성
                cube.setMaterialProperties(0,1.0f);                                 //파란색 object
                cubeSelected.createOnGlThread(context,"dot.obj");
                cubeSelected.setMaterialProperties(1,1.0f);                       //빨간색 object
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                planeRenderer.createOnGlThread(context, "basic.png");
            } catch (IOException e) {
            }
            pointCloud.createOnGlThread(context);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            if(width <= 0 || height <= 0){
                return;
            }
            center_image.invalidate();
            displayRotationHelper.onSurfaceChanged(width, height);
            GLES20.glViewport(0, 0, width, height);
            viewWidth = width;
            viewHeight = height;
            setNowTouchingPointIndex(DEFAULT_VALUE);
        }
        float lightIntensity;
        private int k = 0;              //최초 onDrawFrame 실행 판단 flag 변수
        public void setNowTouchingPointIndex(int index){
            nowTouchingPointIndex = index;
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onDrawFrame(GL10 gl) {

            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            if(viewWidth == 0 || viewWidth == 0){
                return;
            }
            if (session == null) {
                return;
            }
            displayRotationHelper.updateSessionIfNeeded(session);

            try {
                session.setCameraTextureName(backgroundRenderer.getTextureId());
                Frame frame = session.update();
                Camera camera = frame.getCamera();

                backgroundRenderer.draw(frame);
                if (camera.getTrackingState() == TrackingState.PAUSED) {
                    return;
                }
                camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);
                camera.getViewMatrix(viewmtx, 0);
                if(k == 0) {                             //측정 렌더링 화면 처음 출력 시
                    k = 1;
                    if(!check){                         //다시 보지 않기 관련 이벤트 처리
                    Intent intent = new Intent(getApplicationContext(), GuideViewPager.class);
                    startActivity(intent);
                    }
                }

                lightIntensity = frame.getLightEstimate().getPixelIntensity();

                PointCloud pointCloud = frame.acquirePointCloud();
                MeasureActivity.this.pointCloud.update(pointCloud);
                MeasureActivity.this.pointCloud.draw(viewmtx, projmtx);

                pointCloud.release();

                if (messageSnackbar != null) {
                    for (Plane plane : session.getAllTrackables(Plane.class)) {
                        if (plane.getType() == com.google.ar.core.Plane.Type.HORIZONTAL_UPWARD_FACING &&
                                plane.getTrackingState() == TrackingState.TRACKING) {
                            hideLoadingMessage();
                            break;
                        }
                    }
                }
                planeRenderer.drawPlanes(
                        session.getAllTrackables(Plane.class), camera.getDisplayOrientedPose(), projmtx);
                final int Min_scope = Integer.parseInt(((StartMeasure)StartMeasure.mContext).min_scope.get(count));  //측정 최소 길이
                final int Max_scope = Integer.parseInt(((StartMeasure)StartMeasure.mContext).max_scope.get(count));  //측정 최대 길이
                Pose point0 = null;
                if(anchors.size() < 2){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn_fab.setEnabled(true);
                            btn_next.setEnabled(false);
                        }
                    });
                }
                else if(!btn_next.isEnabled()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btn_fab.setEnabled(false);
                            btn_next.setEnabled(true);
                        }
                    });
                }
                if (anchors != null && !anchors.isEmpty()) {
                    if (nowTouchingPointIndex != DEFAULT_VALUE) {
                        drawObj(getPose(anchors.get(nowTouchingPointIndex)),cubeSelected, viewmtx, projmtx, lightIntensity);
                        checkIfHit(cubeSelected, nowTouchingPointIndex);
                    }
                    int total = 0;                    //두 점 사이의 거리 결과 변수
                    point0 = getPose(anchors.get(0));   //첫 번째 점 찍기
                    drawObj(point0, cube, viewmtx, projmtx, lightIntensity);
                    checkIfHit(cube, 0);
                    for (int i = 1; i < anchors.size(); i++) {
                        Pose point1 = getPose(anchors.get(i));  //두 번째 점 찍기
                        drawObj(point1, cube, viewmtx, projmtx, lightIntensity);
                        checkIfHit(cube, i);
                        drawLine(point0, point1, viewmtx, projmtx);
                        float distanceCm = ((int) (getDistance(point0, point1) * 1000)) / 10.0f;
                        total += distanceCm;
                        final float finalTotal = total;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                distance.setText("\t\t"+ Math.round(finalTotal) + "cm\t\t");   //현재 두 점 사이의 거리 출력
                            }
                        });
                        result[count] = Math.round(total);
                    }
                }
                MotionEvent tap = queuedSingleTaps.poll();
                int i = 0;
                if (anchors.size() <= 1 && tap == null && camera.getTrackingState() == TrackingState.TRACKING) {
                    for (HitResult hit : frame.hitTest(getResources().getDisplayMetrics().widthPixels / 2.0f,
                            getResources().getDisplayMetrics().heightPixels / 2.0f)) {
                        Trackable trackable = hit.getTrackable();
                        if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())
                                || (trackable instanceof Point
                                && ((Point) trackable).getOrientationMode()
                                == Point.OrientationMode.ESTIMATED_SURFACE_NORMAL)) {
                            if(point0 != null && anchors.size() == 1) {             //만약 첫 번째 점이 이미 찍힌 상황이면 두 번째 점만 찍는다.
                                Pose point1 = getPose(hit.createAnchor());
                                drawLine(point0, point1, viewmtx, projmtx);
                                final float distanceCm = ((int) (getDistance(point0, point1) * 1000)) / 10.0f;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(distanceCm<Min_scope || distanceCm > Max_scope) {
                                            if(anchors.size() == 1){
                                                btn_fab.setEnabled(false);
                                            }
                                            distance.setText(Math.round(distanceCm) + "cm" + "\n\t\t측정 가능한 범위에서 측정해주세요.\t\t\n\t\t측정 가능 범위는 " + (Min_scope) + "~" + (Max_scope) + "cm 입니다.\t\t");
                                        }
                                        else{
                                            btn_fab.setEnabled(true);
                                            distance.setText(Math.round(distanceCm) + "cm");
                                        }
                                        distance.setGravity(Gravity.CENTER);
                                    }
                                });
                            }
                            if(Cap_flag == 1 && anchors.size() < 2) {               //만약 점이 1개 이하로 찍혀있을 때 무조건 점 추가
                                anchors.add(hit.createAnchor());
                                nowTouchingPointIndex = anchors.size() - 1;
                                Cap_flag = 0;                                        //Cap_flag 초기화
                                break;
                            }
                        }
                    }
                } else if(anchors.size() == 2 && t_flag == 1){                      //점 꾹 눌렀을 때 점을 이동시킬 수 있다.
                    for (HitResult hit : frame.hitTest(x,y)) {
                        handleMoveEvent(nowTouchingPointIndex,hit);
                    }
                }
            } catch(Throwable t) {
            }
        }
        private void handleMoveEvent(int nowSelectedIndex, HitResult hit){          //점이 손가락을 따라오는 이벤트 처리
            try {
                if (nowTouchingPointIndex == DEFAULT_VALUE) {
                    return;
                }
                if (anchors.size() > nowSelectedIndex && t_flag == 1) {
                    Anchor anchor = anchors.remove(nowSelectedIndex);
                    anchor.detach();
                    setPoseDataToTempArray(getPose(anchor));
                    anchors.add(nowSelectedIndex,hit.createAnchor());
                    Pose point0 = getPose(anchors.get(0));
                    Pose point1 = getPose(anchors.get(1));
                    final float distanceCm = ((int) (getDistance(point0, point1) * 1000)) / 10.0f;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            distance.setText("\t\t"+Math.round(distanceCm)+"cm\t\t");
                        }
                    });
                }
            } catch (NotTrackingException e1) {
                e1.printStackTrace();
            }
        }
        //카메라 시점과 해당 카메라 시점에 따른 좌표를 가져오는 함수
        private final float[] mPoseTranslation = new float[3];
        private final float[] mPoseRotation = new float[4];
        private Pose getPose(Anchor anchor){
            Pose pose = anchor.getPose();
            pose.getTranslation(mPoseTranslation, 0);
            pose.getRotationQuaternion(mPoseRotation, 0);
            return new Pose(mPoseTranslation, mPoseRotation);
        }

        private void setPoseDataToTempArray(Pose pose){
            pose.getTranslation(tempTranslation, 0);
            pose.getRotationQuaternion(tempRotation, 0);
        }
        //두 점사이의 선을 그리는 함수
        private void drawLine(Pose pose0, Pose pose1, float[] viewmtx, float[] projmtx){
            float lineWidth = 0.002f;
            float lineWidthH = lineWidth / viewHeight * viewWidth;
            rectRenderer.setVerts(
                    pose0.tx() - lineWidth, pose0.ty() + lineWidthH, pose0.tz() - lineWidth,
                    pose0.tx() + lineWidth, pose0.ty() + lineWidthH, pose0.tz() + lineWidth,
                    pose1.tx() + lineWidth, pose1.ty() + lineWidthH, pose1.tz() + lineWidth,
                    pose1.tx() - lineWidth, pose1.ty() + lineWidthH, pose1.tz() - lineWidth,
                    pose0.tx() - lineWidth, pose0.ty() - lineWidthH, pose0.tz() - lineWidth,
                    pose0.tx() + lineWidth, pose0.ty() - lineWidthH, pose0.tz() + lineWidth,
                    pose1.tx() + lineWidth, pose1.ty() - lineWidthH, pose1.tz() + lineWidth,
                    pose1.tx() - lineWidth, pose1.ty() - lineWidthH, pose1.tz() - lineWidth
            );
            rectRenderer.draw(viewmtx, projmtx);
        }
        //찍은 점에 큐브를 그리는 함수, 현재 카메라 시점, 각도, 빛의 방향 고려해서 그린다.
        private void drawObj(Pose pose, ObjectRenderer renderer, float[] cameraView, float[] cameraPerspective, float lightIntensity) {
            pose.toMatrix(anchorMatrix, 0);
            renderer.updateModelMatrix(anchorMatrix, 0.1f);
            renderer.draw(cameraView, cameraPerspective, lightIntensity);
        }
        //현재 선택된 점인지 판단하는 함수
        private void checkIfHit(ObjectRenderer renderer, int cubeIndex){
            if(isMVPMatrixHitMotionEvent(renderer.getModelViewProjectionMatrix(), queuedLongPress.peek())){
                nowTouchingPointIndex = cubeIndex;
                queuedLongPress.poll();
                t_flag = 1;
            }else if(isMVPMatrixHitMotionEvent(renderer.getModelViewProjectionMatrix(), queuedSingleTaps.peek())){
                nowTouchingPointIndex = cubeIndex;
                queuedSingleTaps.poll();
            }
        }

        private boolean isMVPMatrixHitMotionEvent(float[] ModelViewProjectionMatrix, MotionEvent event){
            if(event == null){
                return false;
            }
            Matrix.multiplyMV(vertexResult, 0, ModelViewProjectionMatrix, 0, centerVertexOfCube, 0);
            float radius = (viewWidth / 2) * (cubeHitAreaRadius/vertexResult[3]);
            float dx = event.getX() - (viewWidth / 2) * (1 + vertexResult[0]/vertexResult[3]);
            float dy = event.getY() - (viewHeight / 2) * (1 - vertexResult[1]/vertexResult[3]);
            double distance = Math.sqrt(dx * dx + dy * dy);
            return distance < radius;
        }
        //두 점 사이의 거리를 구하는 함수
        private double getDistance(Pose pose0, Pose pose1){
            float dx = pose0.tx() - pose1.tx();
            float dy = pose0.ty() - pose1.ty();
            float dz = pose0.tz() - pose1.tz();
            return Math.sqrt(dx * dx + dz * dz + dy * dy);
        }
    }
}

