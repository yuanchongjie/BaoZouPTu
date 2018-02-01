package a.baozouptu;

import com.lgc.memorynote.Word;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    int a;
    int aa[]=new int[2];
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        System.out.println(a);
        System.out.println(aa[0]);
        Word word = new Word();
        word.setMeaningFromUser("@v @sheng #相信，信任#\n @v @guai @sheng #赞颂，把...归功于#\n@n #信任，学分，声望#");
        System.out.println(word.jsonMeaning);


    }
}