package labs.pm.data;

public record Review(Rating rating, String comments) implements Comparable<Review>
{
    @Override
    public int compareTo(Review other) {
        return other.rating.ordinal() - rating.ordinal();
    }
}
