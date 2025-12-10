package com.dotabuildtracker.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.dotabuildtracker.data.model.ItemBuild;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ItemBuildDao_Impl implements ItemBuildDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ItemBuild> __insertionAdapterOfItemBuild;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBuildsByPlayerId;

  public ItemBuildDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfItemBuild = new EntityInsertionAdapter<ItemBuild>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `item_builds` (`id`,`playerId`,`heroName`,`heroId`,`matchCount`,`items`,`timestamp`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ItemBuild entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPlayerId());
        statement.bindString(3, entity.getHeroName());
        statement.bindLong(4, entity.getHeroId());
        statement.bindLong(5, entity.getMatchCount());
        statement.bindLong(7, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfDeleteBuildsByPlayerId = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM item_builds WHERE playerId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertBuild(final ItemBuild build, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfItemBuild.insert(build);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertBuilds(final List<ItemBuild> builds,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfItemBuild.insert(builds);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBuildsByPlayerId(final String playerId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBuildsByPlayerId.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, playerId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteBuildsByPlayerId.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ItemBuild>> getBuildsByPlayerId(final String playerId) {
    final String _sql = "SELECT * FROM item_builds WHERE playerId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, playerId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"item_builds"}, new Callable<List<ItemBuild>>() {
      @Override
      @NonNull
      public List<ItemBuild> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfHeroName = CursorUtil.getColumnIndexOrThrow(_cursor, "heroName");
          final int _cursorIndexOfHeroId = CursorUtil.getColumnIndexOrThrow(_cursor, "heroId");
          final int _cursorIndexOfMatchCount = CursorUtil.getColumnIndexOrThrow(_cursor, "matchCount");
          final int _cursorIndexOfItems = CursorUtil.getColumnIndexOrThrow(_cursor, "items");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ItemBuild> _result = new ArrayList<ItemBuild>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ItemBuild _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPlayerId;
            _tmpPlayerId = _cursor.getString(_cursorIndexOfPlayerId);
            final String _tmpHeroName;
            _tmpHeroName = _cursor.getString(_cursorIndexOfHeroName);
            final int _tmpHeroId;
            _tmpHeroId = _cursor.getInt(_cursorIndexOfHeroId);
            final int _tmpMatchCount;
            _tmpMatchCount = _cursor.getInt(_cursorIndexOfMatchCount);
            final List<String> _tmpItems;
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ItemBuild(_tmpId,_tmpPlayerId,_tmpHeroName,_tmpHeroId,_tmpMatchCount,_tmpItems,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getLatestBuild(final String playerId,
      final Continuation<? super ItemBuild> $completion) {
    final String _sql = "SELECT * FROM item_builds WHERE playerId = ? ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, playerId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ItemBuild>() {
      @Override
      @Nullable
      public ItemBuild call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfHeroName = CursorUtil.getColumnIndexOrThrow(_cursor, "heroName");
          final int _cursorIndexOfHeroId = CursorUtil.getColumnIndexOrThrow(_cursor, "heroId");
          final int _cursorIndexOfMatchCount = CursorUtil.getColumnIndexOrThrow(_cursor, "matchCount");
          final int _cursorIndexOfItems = CursorUtil.getColumnIndexOrThrow(_cursor, "items");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final ItemBuild _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPlayerId;
            _tmpPlayerId = _cursor.getString(_cursorIndexOfPlayerId);
            final String _tmpHeroName;
            _tmpHeroName = _cursor.getString(_cursorIndexOfHeroName);
            final int _tmpHeroId;
            _tmpHeroId = _cursor.getInt(_cursorIndexOfHeroId);
            final int _tmpMatchCount;
            _tmpMatchCount = _cursor.getInt(_cursorIndexOfMatchCount);
            final List<String> _tmpItems;
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result = new ItemBuild(_tmpId,_tmpPlayerId,_tmpHeroName,_tmpHeroId,_tmpMatchCount,_tmpItems,_tmpTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ItemBuild>> getAllBuilds() {
    final String _sql = "SELECT * FROM item_builds";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"item_builds"}, new Callable<List<ItemBuild>>() {
      @Override
      @NonNull
      public List<ItemBuild> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPlayerId = CursorUtil.getColumnIndexOrThrow(_cursor, "playerId");
          final int _cursorIndexOfHeroName = CursorUtil.getColumnIndexOrThrow(_cursor, "heroName");
          final int _cursorIndexOfHeroId = CursorUtil.getColumnIndexOrThrow(_cursor, "heroId");
          final int _cursorIndexOfMatchCount = CursorUtil.getColumnIndexOrThrow(_cursor, "matchCount");
          final int _cursorIndexOfItems = CursorUtil.getColumnIndexOrThrow(_cursor, "items");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ItemBuild> _result = new ArrayList<ItemBuild>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ItemBuild _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPlayerId;
            _tmpPlayerId = _cursor.getString(_cursorIndexOfPlayerId);
            final String _tmpHeroName;
            _tmpHeroName = _cursor.getString(_cursorIndexOfHeroName);
            final int _tmpHeroId;
            _tmpHeroId = _cursor.getInt(_cursorIndexOfHeroId);
            final int _tmpMatchCount;
            _tmpMatchCount = _cursor.getInt(_cursorIndexOfMatchCount);
            final List<String> _tmpItems;
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ItemBuild(_tmpId,_tmpPlayerId,_tmpHeroName,_tmpHeroId,_tmpMatchCount,_tmpItems,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
