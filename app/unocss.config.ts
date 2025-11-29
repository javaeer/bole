import presetWeapp from "unocss-preset-weapp";
import { extractorAttributify, transformerClass } from "unocss-preset-weapp/transformer";

const { presetWeappAttributify, transformerAttributify } = extractorAttributify();

export default {
  presets: [
    // https://github.com/MellowCo/unocss-preset-weapp
    presetWeapp(),
    // attributify autocomplete
    presetWeappAttributify(),
  ],
  shortcuts: [
    {
      "flex-center": "flex justify-center items-center flex-wrap",
      "flex-start": "flex justify-start items-center",
      "flex-end": "flex justify-end items-center",
      "flex-col-center": "flex flex-col justify-center items-center",
      "flex-between": "flex justify-between items-center",
      "fixed-bottom": "fixed bottom-0 left-0 right-0",
      "absolute-center": "absolute inset-0 flex justify-center items-center",
    },
  ],

  transformers: [
    // https://github.com/MellowCo/unocss-preset-weapp/tree/main/src/transformer/transformerAttributify
    transformerAttributify(),

    // https://github.com/MellowCo/unocss-preset-weapp/tree/main/src/transformer/transformerClass
    transformerClass(),
  ],
};
