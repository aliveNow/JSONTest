package ru.skypathway.jsontest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import ru.skypathway.jsontest.data.BaseLoader.LoaderResult;
import ru.skypathway.jsontest.data.BaseObjectLoader;
import ru.skypathway.jsontest.data.BaseObjectType;
import ru.skypathway.jsontest.data.ExceptionWrapper;
import ru.skypathway.jsontest.data.dao.BaseObject;
import ru.skypathway.jsontest.utils.Constants;
import ru.skypathway.jsontest.utils.InputFilterMinMax;
import ru.skypathway.jsontest.utils.TextChangedListener;
import ru.skypathway.jsontest.utils.Utils;

// FIXME: 15.10.17 добавить описание по макетам
/**
 * Created by samsmariya on 11.10.17.
 *
 * Базовый фрагмент для загрузки и отображения объектов, наследующих от {@link BaseObject}.
 * Activity, содержащие этот фрагмент должны реализовывать интерфейсы:
 * {@link BaseObjectFragment.BaseObjectFragmentDelegate},
 * {@link BaseObjectFragment.BaseObjectFragmentListener}.
 */
public abstract class BaseObjectFragment<T extends BaseObject> extends Fragment
        implements LoaderManager.LoaderCallbacks<LoaderResult<T>>,
        View.OnFocusChangeListener,
        TextView.OnEditorActionListener {
    private static final String TAG = BaseObjectFragment.class.getSimpleName();
    protected static final String LOADING_ERROR_EXTRA = "loading_error";

    protected BaseObjectFragmentDelegate mDelegate;
    protected BaseObjectFragmentListener mListener;

    protected List<T> mObjects;
    protected int[] mObjectIds;
    protected final BaseObjectType mType = getBaseObjectType();

    protected ExceptionWrapper mLoadingError;
    protected boolean shouldShowError; // показывать ли секцию с ошибкой загрузки
    protected boolean shouldShowResult; // показывать ли секцию с результатом загрузки

    protected View mLayoutResults; // секция загрузки
    protected View mLayoutEnterId;
    protected TextInputLayout mLayoutEditId;
    protected EditText mEditId;
    protected Button mButtonConfirmed;
    protected ProgressBar mProgressBar;

    protected View mLayoutError; // секция ошибки
    protected TextView mTextErrorDescription;
    protected TextView mTextError;
    protected Button mButtonTryAgain;

    public BaseObjectFragment() {}

    //region Overridden Fragment Methods
    //--------------------------------------------------------------------------------
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mObjectIds = savedInstanceState.getIntArray(Constants.Extras.OBJECT_IDS);
            mLoadingError = (ExceptionWrapper) savedInstanceState.getSerializable(LOADING_ERROR_EXTRA);
        }
        onPrepareViews(savedInstanceState);
        if (!Utils.isEmptyArray(mObjectIds)) {
            getLoaderManager().initLoader(getLoaderId(), null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(Constants.Extras.OBJECT_IDS, mObjectIds);
        outState.putSerializable(LOADING_ERROR_EXTRA, mLoadingError);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseObjectFragmentDelegate) {
            mDelegate = (BaseObjectFragmentDelegate) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BaseObjectFragmentDelegate");
        }
        if (context instanceof BaseObjectFragmentListener) {
            mListener = (BaseObjectFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BaseObjectFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDelegate = null;
        mListener = null;
    }
    //--------------------------------------------------------------------------------
    //endregion

    /**
     * Инициализация views и подготовка их к выводу на экран.
     * Вызывается в {@link #onActivityCreated(Bundle)} после того, как восстановлены значения
     * из {@code savedInstanceState}.
     *
     * @param savedInstanceState - передаётся из {@link #onActivityCreated(Bundle)}.
     */
    @CallSuper
    protected void onPrepareViews(Bundle savedInstanceState) {
        mLayoutResults = findViewById(R.id.layout_results);
        Utils.requireNonNull(mLayoutResults, TAG +
                " in view must be layout with id = layout_results");
        mLayoutEnterId = findViewById(R.id.layout_id_enter);
        if (mLayoutEnterId != null) {
            mLayoutEditId = findViewById(R.id.layout_edit_id);
            mEditId = findViewById(R.id.edit_id);
            mButtonConfirmed = findViewById(R.id.button_confirmed);
            mButtonConfirmed.setEnabled(false);
            mButtonConfirmed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onButtonConfirmedClick();
                }
            });
            mLayoutEditId.setHint(getString(R.string.hint_enter_object_id,
                    mType.maxId,
                    Utils.getTypeNameGenitive(getActivity(), mType)));
            mEditId.setFilters(new InputFilter[]
                    {new InputFilterMinMax(mType.minId, mType.maxId)});
            mEditId.setOnEditorActionListener(this);
            mEditId.addTextChangedListener(new TextChangedListener<EditText>(mEditId){
                @Override
                public void onTextChanged(EditText target, Editable s) {
                    mButtonConfirmed.setEnabled(s.length() > 0);
                }
            });
            mEditId.setOnFocusChangeListener(this);
        }
        mLayoutError = findViewById(R.id.layout_error);
        Utils.requireNonNull(mLayoutError, TAG +
                " in view must be layout with id = layout_error");
        mTextError = findViewById(R.id.text_error);
        mTextErrorDescription = findViewById(R.id.text_error_description);
        mButtonTryAgain = findViewById(R.id.button_try_again);
        mLayoutError.setVisibility(View.GONE);
        mButtonTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonTryAgainClick();
            }
        });

        mProgressBar = findViewById(R.id.progress_bar);
        hideProgressBar();
    }

    /**
     * Определяет категорию объекта, используемую при загрузке данных.
     * @return категория объекта, совпадающая с параметром типа фрагмента
     */
    public abstract @NonNull
    BaseObjectType getBaseObjectType();

    /**
     * Отображает ли наследующий фрагмент только один объект или список.
     * @return true - наследующий фрагмент отображает только один объект.
     *         После загрузки данных вызывается {@link #onDataObjectChange(BaseObject)};
     *         false - наследующий фрагмент отображает список объектов.
     *         После загрузки данных вызывается {@link #onDataChange(List)}
     */
    protected abstract boolean isOneObjectFragment();

    /**
     * Вызывается при успешной загрузке, если {@link #isOneObjectFragment()} возвращает false.
     * @param data - загруженный список объектов целиком
     */
    protected void onDataChange(List<T> data) {}

    /**
     * Вызывается при успешной загрузке, если {@link #isOneObjectFragment()} возвращает true.
     * @param data - первый объект из загруженного списка
     */
    protected void onDataObjectChange(T data) {}

    /**
     * Переопределить этот метод, если нужно продолжить загрузку после
     * {@link #onLoadFinished(Loader, LoaderResult)}.
     *
     * @param data - загруженный результат
     * @return true - загрузка продолжается, наследующий фрагмент должен сам вызвать
     *         {@link #hideProgressBar()} после окончания;
     *         false - загрузка окончена
     */
    protected boolean willContinueLoadData(LoaderResult<T> data){
        return false;
    }

    public int getLoaderId() {
        return mType.ordinal();
    }

    protected Loader<LoaderResult<T>> getNewLoader(Bundle args) {
        return new BaseObjectLoader<>(getActivity(), mType, mObjectIds);
    }

    @Override
    public Loader<LoaderResult<T>> onCreateLoader(int id, Bundle args) {
        if (id == getLoaderId()) {
            showProgressBar();
            return getNewLoader(args);
        }
        return null;
    }

    //region LoaderManager.LoaderCallbacks Implementation
    //--------------------------------------------------------------------------------
    @Override
    public void onLoadFinished(Loader<LoaderResult<T>> loader, LoaderResult<T> data) {
        mObjects = data.getResult();
        if (mObjects == null || mObjects.size() == 0) {
            mObjects = null;
            shouldShowResult = false;
        }else {
            shouldShowResult = !mDelegate.onBaseFragmentLoadFinished(this, (List<BaseObject>) mObjects);
        }
        if (shouldShowResult) {
            if (isOneObjectFragment()) {
                onDataObjectChange(mObjects != null ? mObjects.get(0) : null);
            }else {
                onDataChange(mObjects);
            }
        }
        if (data.getError() != null) {
            String errorStr;
            String typeName = Utils.getTypeNameGenitive(getActivity(), mType, mObjectIds.length);
            errorStr = getResources().getString(R.string.msg_loading_failed,
                    typeName,
                    getResources().getQuantityString(R.plurals.plurals_ids, mObjectIds.length),
                    Utils.arrayToString(",", mObjectIds));
            mLoadingError = new ExceptionWrapper(data.getError(), errorStr);
            shouldShowError = !mDelegate.onBaseFragmentLoadingException(this, mLoadingError);
            if (shouldShowError) {
                onLoaderError(mLoadingError);
            }
        }else {
            mLoadingError = null;
            shouldShowError = false;
        }
        if (!willContinueLoadData(data)) {
            hideProgressBar();
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<T>> loader) {
        mObjects = null;
        if (isOneObjectFragment()) {
            onDataObjectChange(null);
        }else {
            onDataChange(null);
        }
        hideProgressBar();
    }
    //--------------------------------------------------------------------------------
    //endregion

    protected void onLoaderError(ExceptionWrapper loadingError) {
        mTextError.setText(loadingError.getLocalizedMessage());
        mTextErrorDescription.setText(loadingError.getCauseDescription());
    }

    protected void onButtonConfirmedClick() {
        int objId = 0;
        try {
            objId = Integer.parseInt(mEditId.getText().toString());
        }catch (NumberFormatException nfe) {
            Log.e(TAG, "something went wrong, check your code " + nfe);
            /* TODO: 15.10.17 диалог на маловероятный случай, что пользователю удастся ввести сюда
             что-нибудь неправильное? Фильтр на EditText даже вставку не даёт сделать не того типа. */
        }
        if (objId > 0) {
            mObjectIds = new int[]{objId};
            restartLoading();
        }
    }

    protected void onButtonTryAgainClick() {
        restartLoading();
    }

    protected void restartLoading() {
        Utils.hideSoftInputKeyboard(getActivity());
        mEditId.clearFocus();
        getLoaderManager().restartLoader(getLoaderId(), null, this);
        mListener.onFragmentGetFocus(this, mButtonConfirmed);
    }

    protected void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mLayoutResults.setVisibility(View.GONE);
        mLayoutError.setVisibility(View.GONE);
    }

    protected void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
        mLayoutResults.setVisibility(shouldShowResult ? View.VISIBLE : View.GONE);
        mLayoutError.setVisibility(shouldShowError ? View.VISIBLE : View.GONE);
    }

    //region Listeners Implementation
    //--------------------------------------------------------------------------------
    // View.OnFocusChangeListener
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mListener.onFragmentGetFocus(this, v);
        }
    }

    // TextView.OnEditorActionListener
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onButtonConfirmedClick();
            return true;
        }
        return false;
    }
    //--------------------------------------------------------------------------------
    //endregion

    //region Helper Methods
    //--------------------------------------------------------------------------------
    protected<T> T findViewById(@IdRes int viewId) {
        return (T) getView().findViewById(viewId);
    }

    protected int getFirstObjectId() {
        if (!Utils.isEmptyArray(mObjectIds)) {
            return mObjectIds[0];
        }
        return 0;
    }
    //--------------------------------------------------------------------------------
    //endregion

    //region Activity Interfaces Declaration
    //--------------------------------------------------------------------------------
    /**
     * Делегат обработки результатов загрузки фрагмента.
     */
    public interface BaseObjectFragmentDelegate {

        /**
         * Вызывается в случае успешной загрузки.
         *
         * @param fragment - экземпляр текущего фрагмента
         * @param list - результат загрузки
         * @return true - результаты загрузки будут обработаны делегатом,
         *                не показывать их во фрагменте;
         *         false - показать результ загрузки в фрагменте.
         */
        boolean onBaseFragmentLoadFinished(BaseObjectFragment fragment, List<BaseObject> list);

        /**
         * Вызывается в случае ошибки во время загрузки.
         *
         * @param fragment - экземпляр текущего фрагмента
         * @param exception - ошибка загрузки - обёрнутое исключение, которое возвращает в
         *                  {@link ExceptionWrapper#getMessage()} локализованное сообщение
         *                  неудавшегося действия, оригинальное исключение можно получить через
         *                  {@link ExceptionWrapper#getCause()}
         * @return true - ошибка будет обработана делегатом, не показывать её во фрагменте;
         *         false - показать ошибку в фрагменте.
         */
        boolean onBaseFragmentLoadingException(BaseObjectFragment fragment,
                                               ExceptionWrapper exception);
    }

    /**
     * Листенер для событий фрагмента.
     */
    public interface BaseObjectFragmentListener {

        /**
         * Вызывается, если фрагмент получил фокус на EditText или была нажата любая кнопка загрузки.
         *
         * @param fragment - экземпляр текущего фрагмента
         * @param view - которое получило фокус
         */
        void onFragmentGetFocus(BaseObjectFragment fragment, View view);
    }
    //--------------------------------------------------------------------------------
    //endregion
}
