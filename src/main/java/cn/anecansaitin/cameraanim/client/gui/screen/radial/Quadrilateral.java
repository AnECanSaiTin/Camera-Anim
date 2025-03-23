package cn.anecansaitin.cameraanim.client.gui.screen.radial;

import org.joml.Vector2f;

public record Quadrilateral(Vector2f p1, Vector2f p2, Vector2f p3, Vector2f p4) {
    public boolean contains(Vector2f vec2f) {
        Vector2f rayEndPoint = new Vector2f(1e9f, vec2f.y());

        Vector2f[][] edges = new Vector2f[][]{
                {p1, p2},
                {p2, p3},
                {p3, p4},
                {p4, p1}
        };

        int intersections = 0;
        for (Vector2f[] edge : edges) {
            if (isIntersecting(vec2f, rayEndPoint, edge[0], edge[1])) {
                intersections++;
            }
        }

        return intersections % 2 == 1;
    }

    private float cross(float v1x, float v1y, float v2x, float v2y) {
        return v1x * v2y - v1y * v2x;
    }

    private boolean isIntersecting(Vector2f p1, Vector2f p2, Vector2f q1, Vector2f q2) {
        float d1 = cross(q1.x() - p1.x(), q1.y() - p1.y(), p2.x() - p1.x(), p2.y() - p1.y());
        float d2 = cross(q2.x() - p1.x(), q2.y() - p1.y(), p2.x() - p1.x(), p2.y() - p1.y());
        float d3 = cross(p1.x() - q1.x(), p1.y() - q1.y(), q2.x() - q1.x(), q2.y() - q1.y());
        float d4 = cross(p2.x() - q1.x(), p2.y() - q1.y(), q2.x() - q1.x(), q2.y() - q1.y());
        return d1 * d2 < 0 && d3 * d4 < 0;
    }
}