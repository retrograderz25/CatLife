package hust.hedspi.oop.game.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import hust.hedspi.oop.game.entities.TriggerZone;
import hust.hedspi.oop.game.entities.NPC;
import hust.hedspi.oop.game.entities.Cat.CatColor;

import java.util.ArrayList;
import java.util.List;

public class MapManager {
    private static MapManager instance;
    private TiledMap currentMap;
    private OrthogonalTiledMapRenderer mapRenderer;
    private List<Rectangle> collisionRectangles;
    private List<TriggerZone> triggerZones;
    private List<NPC> npcs;

    private MapManager() {
        collisionRectangles = new ArrayList<>();
        triggerZones = new ArrayList<>();
        npcs = new ArrayList<>();
    }

    public static MapManager getInstance() {
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }

    public void loadMap(String filePath) {
        if (currentMap != null) {
            currentMap.dispose();
            mapRenderer.dispose();
        }
        
        currentMap = new TmxMapLoader().load(filePath);
        // Unit scale có thể tùy chỉnh tùy vào size của map, thường là 1f cho pixel art
        mapRenderer = new OrthogonalTiledMapRenderer(currentMap, 1f); 
        
        loadCollisions();
        loadTriggers();
    }

    private void loadCollisions() {
        collisionRectangles.clear();
        MapLayer collisionLayer = currentMap.getLayers().get("Collision");
        
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    collisionRectangles.add(((RectangleMapObject) object).getRectangle());
                }
            }
        }
    }

    private void loadTriggers() {
        triggerZones.clear();
        for (NPC npc : npcs) {
            npc.dispose();
        }
        npcs.clear();
        MapLayer triggerLayer = currentMap.getLayers().get("Trigger");
        
        if (triggerLayer != null) {
            CatColor[] colors = CatColor.values();
            int colorIndex = 0;
            
            for (MapObject object : triggerLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) object).getRectangle();
                    String name = object.getName() != null ? object.getName() : "Unknown Trigger";
                    
                    TriggerZone zone = new TriggerZone(rect.x, rect.y, rect.width, rect.height, name);
                    triggerZones.add(zone);
                    
                    // Thêm NPC đứng tại vị trí trigger
                    CatColor color = colors[colorIndex % colors.length];
                    NPC npc = new NPC(rect.x, rect.y, 12, 12, color, "NPC " + name);
                    npcs.add(npc);
                    colorIndex++;
                }
            }
        }
    }

    public void render(OrthographicCamera camera) {
        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        }
    }

    public List<Rectangle> getCollisionRectangles() {
        return collisionRectangles;
    }

    public List<TriggerZone> getTriggerZones() {
        return triggerZones;
    }

    public List<hust.hedspi.oop.game.entities.NPC> getNpcs() {
        return npcs;
    }

    public float getMapPixelWidth() {
        if (currentMap == null) return 0f;
        int width = currentMap.getProperties().get("width", Integer.class);
        int tileWidth = currentMap.getProperties().get("tilewidth", Integer.class);
        return Math.max(width * tileWidth, 750f); // 750 là chiều rộng của bức ảnh street.png
    }

    public float getMapPixelHeight() {
        if (currentMap == null) return 0f;
        int height = currentMap.getProperties().get("height", Integer.class);
        int tileHeight = currentMap.getProperties().get("tileheight", Integer.class);
        return Math.max(height * tileHeight, 415f); // 415 là chiều cao của bức ảnh street.png
    }

    public void dispose() {
        if (currentMap != null) {
            currentMap.dispose();
            mapRenderer.dispose();
        }
        for (hust.hedspi.oop.game.entities.NPC npc : npcs) {
            npc.dispose();
        }
        npcs.clear();
    }
}
