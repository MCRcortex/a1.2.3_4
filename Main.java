import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    static  int rainforest=0;
    static  int swampland=1;
    static  int seasonalForest=2;
    static  int forest=3;
    static  int savanna=4;
    static  int shrubland=5;
    static  int taiga=6;
    static  int desert=7;
    static  int plains=8;
    static  int tundra=9;

    public static int getBiome(float f, float f1)
    {
        f1 *= f;
        if(f < 0.1F)
        {
            return tundra;
        }
        if(f1 < 0.2F)
        {
            if(f < 0.5F)
            {
                return tundra;
            }
            if(f < 0.95F)
            {
                return savanna;
            } else
            {
                return desert;
            }
        }
        if(f1 > 0.5F && f < 0.7F)
        {
            return swampland;
        }
        if(f < 0.5F)
        {
            return taiga;
        }
        if(f < 0.97F)
        {
            if(f1 < 0.35F)
            {
                return shrubland;
            } else
            {
                return forest;
            }
        }
        if(f1 < 0.45F)
        {
            return plains;
        }
        if(f1 < 0.9F)
        {
            return seasonalForest;
        } else
        {
            return rainforest;
        }
    }

    private static int biomeLookupConverterTable[] = new int[4096];
    static
    {
        for(int i = 0; i < 64; i++)
        {
            for(int j = 0; j < 64; j++)
            {
                biomeLookupConverterTable[i + j * 64] = getBiome((float)i / 63F, (float)j / 63F);
            }

        }
    }






    AtomicInteger total=new AtomicInteger(0);

    public static void main(String[] args)
    {
        new Main().doStuff();
    }
    static final int total_therads = 8;
    public void doThread(int Id)
    {
        for(long worldSeed=Id;worldSeed<100000;worldSeed+=total_therads) {
            NoiseGeneratorOctaves2 TemperatureNoise = new NoiseGeneratorOctaves2(new Random(worldSeed * 9871L), 4);
            NoiseGeneratorOctaves2 HumidityNoise = new NoiseGeneratorOctaves2(new Random(worldSeed * 39811L), 4);
            NoiseGeneratorOctaves2 field_4192_g = new NoiseGeneratorOctaves2(new Random(worldSeed * 0x84a59L), 2);
            boolean valid =checkAreaAround0(TemperatureNoise,HumidityNoise,field_4192_g);
            if(valid) {
                System.out.println(total.addAndGet(1));
            }
        }
    }
    public void doStuff()
    {
        for(int i=0;i<total_therads;i++) {
            int finalI = i;
            (new Thread(() -> doThread(finalI))).start();
        }
    }



    public boolean checkAreaAround0(NoiseGeneratorOctaves2 TemperatureNoise,NoiseGeneratorOctaves2 HumidityNoise,NoiseGeneratorOctaves2 field_4192_g) {//i,j are position
        int k=32;//width and length of generated area
        int l=32;
        int i=-16;//x
        int j=-32;//z



        double[]  temperature = TemperatureNoise.func_4112_a(null, i, j, k, l, 0.02500000037252903D, 0.02500000037252903D, 0.25D);
        double[]  humidity = HumidityNoise.func_4112_a(null, i, j, k, l, 0.05000000074505806D, 0.05000000074505806D, 0.33333333333333331D);
        double[] field_4196_c = field_4192_g.func_4112_a(null, i, j, k, l, 0.25D, 0.25D, 0.58823529411764708D);

        int i1 = 0;
        for(int j1 = 0; j1 < k; j1++)
        {
            for(int k1 = 0; k1 < l; k1++)
            {
                double d = field_4196_c[i1] * 1.1000000000000001D + 0.5D;
                double d1 = 0.01D;
                double d2 = 1.0D - d1;
                double d3 = (temperature[i1] * 0.14999999999999999D + 0.69999999999999996D) * d2 + d * d1;
                d1 = 0.002D;
                d2 = 1.0D - d1;
                double d4 = (humidity[i1] * 0.14999999999999999D + 0.5D) * d2 + d * d1;
                d3 = 1.0D - (1.0D - d3) * (1.0D - d3);
                if(d3 < 0.0D)
                {
                    d3 = 0.0D;
                }
                if(d4 < 0.0D)
                {
                    d4 = 0.0D;
                }
                if(d3 > 1.0D)
                {
                    d3 = 1.0D;
                }
                if(d4 > 1.0D)
                {
                    d4 = 1.0D;
                }
                temperature[i1] = d3;
                humidity[i1] = d4;
                int a = (int)(d3 * 63D);
                int b = (int)(d4 * 63D);
                i1++;
                if(biomeLookupConverterTable[a + b * 64]!=plains)
                {
                    //System.out.println(biomeLookupConverterTable[a + b * 64]);
                    return false;
                }
            }
        }
        return true;
    }
}
