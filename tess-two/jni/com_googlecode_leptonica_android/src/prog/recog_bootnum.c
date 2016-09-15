/*====================================================================*
 -  Copyright (C) 2001 Leptonica.  All rights reserved.
 -
 -  Redistribution and use in source and binary forms, with or without
 -  modification, are permitted provided that the following conditions
 -  are met:
 -  1. Redistributions of source code must retain the above copyright
 -     notice, this list of conditions and the following disclaimer.
 -  2. Redistributions in binary form must reproduce the above
 -     copyright notice, this list of conditions and the following
 -     disclaimer in the documentation and/or other materials
 -     provided with the distribution.
 -
 -  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 -  ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 -  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 -  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL ANY
 -  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 -  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 -  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 -  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 -  OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 -  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 -  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *====================================================================*/

/*
 *  recog_bootnum.c
 *
 *     Makes bootnum pixa from stored labelled data.
 *
 *     Makes code for generating and compiling the pixa that
 *     are used to make the boot recognizer.  The final output is
 *     bootnum101.* and bootnum102.*.  These are easily edited,
 *     combining the .c and .h files into a single .c file,
 *     to make the files bootnum1.c and bootnum2.c.
 */

#include "allheaders.h"
#include "bmfdata.h"

PIXA  *MakeBootnum1(void);
PIXA  *MakeBootnum2(void);


l_int32 main(int    argc,
             char **argv)
{
char         *dir, *path;
char         *str1 = NULL;
char         *chara, *chara2;
char          buf[256];
l_uint8      *data1, *data2, *data3, *data4;
l_int32       w, h, i, n, bx, by, bw, bh, nchar, nbytes, ret;
l_int32      *rtable64;
l_uint32      pixval;
size_t        nbytes1, nbytes2, nout, nout2, nout3;
L_BMF        *bmf;
BOX          *box1, *box2;
BOXA         *boxa;
PIX          *pix, *pixs, *pixm, *pixg, *pixd;
PIX          *pix0, *pix1, *pix2, *pix3, *pix4, *pix5, *pix6;
PIXA         *pixas, *pixa1, *pixa2, *pixa3;
L_RECOG      *recog;
L_STRCODE    *strc;

    if (argc != 1) {
        fprintf(stderr, " Syntax: recog_bootnum\n");
        return 1;
    }

    lept_mkdir("lept/recog/digits");

    /* ----------------------- Bootnum 1 --------------------- */
        /* Make the bootnum pixa from the images */
    pixa1 = MakeBootnum1();
    pixaWrite("/tmp/lept/recog/digits/bootnum1.pa", pixa1);
    pix1 = pixaDisplayTiledWithText(pixa1, 1500, 1.0, 10, 2, 6, 0xff000000);
    pixDisplay(pix1, 100, 0);
    pixDestroy(&pix1);
    pixaDestroy(&pixa1);

        /* Generate the code to make the bootnum1 pixa.
         * Note: the actual code we use is in bootnumgen1.c, and
         * has already been compiled into the library. */
    strc = strcodeCreate(101);  /* arbitrary integer */
    strcodeGenerate(strc, "/tmp/lept/recog/digits/bootnum1.pa", "PIXA");
    strcodeFinalize(&strc, "/tmp/lept/auto");
    lept_free(strc);

        /* Generate the bootnum1 pixa from the generated code */
    pixa1 = (PIXA *)l_bootnum_gen1();
    pix1 = pixaDisplayTiledWithText(pixa1, 1500, 1.0, 10, 2, 6, 0xff000000);
/*    pix1 = pixaDisplayTiled(pixa1, 1500, 0, 30); */
    pixDisplay(pix1, 100, 0);
    pixDestroy(&pix1);

        /* Extend the bootnum1 pixa by erosion */
    pixa3 = pixaExtendIterative(pixa1, L_MORPH_ERODE, 2, NULL, 1);
    pix1 = pixaDisplayTiledWithText(pixa3, 1500, 1.0, 10, 2, 6, 0xff000000);
    pixDisplay(pix1, 100, 0);
    pixDestroy(&pix1);
    pixaDestroy(&pixa1);
    pixaDestroy(&pixa3);

    /* ----------------------- Bootnum 2 --------------------- */
        /* Make the bootnum pixa from the images */
    L_INFO("the 4 errors below are due to bad input\n", "recog_bootnum");
    pixa2 = MakeBootnum2();
    pix1 = pixaDisplayTiledWithText(pixa2, 1500, 1.0, 10, 2, 6, 0xff000000);
    pixDisplay(pix1, 100, 700);
    pixDestroy(&pix1);
    pixaDestroy(&pixa2);

        /* Generate the code to make the bootnum2 pixa.
         * Note: the actual code we use is in bootnumgen2.c. */
    strc = strcodeCreate(102);  /* another arbitrary integer */
    strcodeGenerate(strc, "/tmp/lept/recog/digits/bootnum2.pa", "PIXA");
    strcodeFinalize(&strc, "/tmp/lept/auto");
    lept_free(strc);

        /* Generate the bootnum2 pixa from the generated code */
    pixa2 = (PIXA *)l_bootnum_gen2();
/*    pix1 = pixaDisplayTiled(pixa2, 1500, 0, 30);  */
    pix1 = pixaDisplayTiledWithText(pixa2, 1500, 1.0, 10, 2, 6, 0xff000000);
    pixDisplay(pix1, 100, 700);
    pixDestroy(&pix1);
    pixaDestroy(&pixa2);


#if 0
    pixas = (PIXA *)l_bootnum_gen1();
/*    pixas = pixaRead("recog/digits/bootnum1.pa"); */
    pixaWrite("/tmp/junk.pa", pixas);
    pixa1 = pixaRead("/tmp/junk.pa");
    pixaWrite("/tmp/junk1.pa", pixa1);
    pixa = pixaRead("/tmp/junk1.pa");
    n = pixaGetCount(pixa);
    for (i = 0; i < n; i++) {
        pix = pixaGetPix(pixa, i, L_CLONE);
        fprintf(stderr, "i = %d, text = %s\n", i, pixGetText(pix));
        pixDestroy(&pix);
    }
#endif

    return 0;
}


PIXA *MakeBootnum1(void)
{
const char  *str;
PIXA        *pixa1, *pixa2, *pixa3;

    pixa1 = pixaRead("recog/digits/digit_set02.pa");
    str = "10, 27, 35, 45, 48, 74, 79, 97, 119, 124, 148";
    pixa3 = pixaSelectWithString(pixa1, str, NULL);
    pixaDestroy(&pixa1);

    pixa1 = pixaRead("recog/digits/digit_set03.pa");
    str = "2, 15, 30, 50, 60, 75, 95, 105, 121, 135";
    pixa2 = pixaSelectWithString(pixa1, str, NULL);
    pixaJoin(pixa3, pixa2, 0, -1);
    pixaDestroy(&pixa1);
    pixaDestroy(&pixa2);

    pixa1 = pixaRead("recog/digits/digit_set05.pa");
    str = "0, 15, 30, 49, 60, 75, 90, 105, 120, 135";
    pixa2 = pixaSelectWithString(pixa1, str, NULL);
    pixaJoin(pixa3, pixa2, 0, -1);
    pixaDestroy(&pixa1);
    pixaDestroy(&pixa2);

    pixa1 = pixaRead("recog/digits/digit_set06.pa");
    str = "4, 15, 30, 48, 60, 78, 90, 105, 120, 135";
    pixa2 = pixaSelectWithString(pixa1, str, NULL);
    pixaJoin(pixa3, pixa2, 0, -1);
    pixaDestroy(&pixa1);
    pixaDestroy(&pixa2);

    pixa1 = pixaRead("recog/digits/digit_set07.pa");
    str = "3, 15, 30, 45, 60, 77, 78, 91, 105, 120, 149";
    pixa2 = pixaSelectWithString(pixa1, str, NULL);
    pixaJoin(pixa3, pixa2, 0, -1);
    pixaDestroy(&pixa1);
    pixaDestroy(&pixa2);

    pixa1 = pixaRead("recog/digits/digit_set08.pa");
    str = "0, 20, 30, 45, 60, 75, 90, 106, 121, 135";
    pixa2 = pixaSelectWithString(pixa1, str, NULL);
    pixaJoin(pixa3, pixa2, 0, -1);
    pixaDestroy(&pixa1);
    pixaDestroy(&pixa2);

    pixa1 = pixaRead("recog/digits/digit_set09.pa");
    str = "0, 20, 32, 47, 54, 63, 75, 91, 105, 125, 136";
    pixa2 = pixaSelectWithString(pixa1, str, NULL);
    pixaJoin(pixa3, pixa2, 0, -1);
    pixaDestroy(&pixa1);
    pixaDestroy(&pixa2);

    pixa1 = pixaRead("recog/digits/digit_set11.pa");
    str = "0, 15, 36, 46, 62, 63, 76, 91, 106, 123, 135";
    pixa2 = pixaSelectWithString(pixa1, str, NULL);
    pixaJoin(pixa3, pixa2, 0, -1);
    pixaDestroy(&pixa1);
    pixaDestroy(&pixa2);

    pixa1 = pixaRead("recog/digits/digit_set12.pa");
    str = "1, 20, 31, 45, 61, 75, 95, 107, 120, 135";
    pixa2 = pixaSelectWithString(pixa1, str, NULL);
    pixaJoin(pixa3, pixa2, 0, -1);
    pixaDestroy(&pixa1);
    pixaDestroy(&pixa2);

    pixa1 = pixaRead("recog/digits/digit_set13.pa");
    str = "1, 16, 31, 48, 63, 78, 98, 105, 123, 136";
    pixa2 = pixaSelectWithString(pixa1, str, NULL);
    pixaJoin(pixa3, pixa2, 0, -1);
    pixaDestroy(&pixa1);
    pixaDestroy(&pixa2);

    return pixa3;
}


PIXA *MakeBootnum2(void)
{
char     *fname;
l_int32   i, n, w, h;
BOX      *box;
PIX      *pix;
PIXA     *pixa;
L_RECOG  *recog;
SARRAY   *sa;

        /* Phase 1: generate recog from the digit data */
    recog = recogCreate(20, 32, L_USE_ALL, 120, 1);
    sa = getSortedPathnamesInDirectory("recog/bootnums", "png", 0, 0);
    n = sarrayGetCount(sa);
    for (i = 0; i < n; i++) {
            /* Read each pix: grayscale, multi-character, labelled */
        fname = sarrayGetString(sa, i, L_NOCOPY);
        if ((pix = pixRead(fname)) == NULL) {
            fprintf(stderr, "Can't read %s\n", fname);
            continue;
        }

            /* Convert to a set of 1 bpp, single character, labelled */
        pixGetDimensions(pix, &w, &h, NULL);
        box = boxCreate(0, 0, w, h);
        recogTrainLabelled(recog, pix, box, NULL, 1, 0);
        pixDestroy(&pix);
        boxDestroy(&box);
    }
    recogTrainingFinished(recog, 1);
    sarrayDestroy(&sa);

        /* Phase 2: generate pixa consisting of 1 bpp, single character pix */
    recogWritePixa("/tmp/lept/recog/digits/bootnum2.pa", recog);
    pixa = pixaRead("/tmp/lept/recog/digits/bootnum2.pa");
    recogDestroy(&recog);
    return pixa;
}


