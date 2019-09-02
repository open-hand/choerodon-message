import _ from 'lodash';

/**
 * 判断params和filters中是否含有特殊字符
 * @param params 表格params
 * @param filters 表格filters
 * @returns {*}
 *
 * 如果加了其他的方法就把下面这条eslint删了
 */
/* eslint-disable-next-line */
export function handleFiltersParams(params, filters) {
  /* eslint-disable-next-line */
  const pattern = new RegExp(/[\{\}\|\^\`\\]/g);
  const targetParams = params.find(item => pattern.test(item));
  const targetfilters = Object.values(filters).find(item => !_.isEmpty(item) && pattern.test(item[0]));
  return targetParams || targetfilters;
}
/**
 * 生成指定长度的随机字符串
 * @param len 字符串长度
 * @returns {string}
 */
export function randomString(len = 32) {
  let code = '';
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  const maxPos = chars.length;
  for (let i = 0; i < len; i += 1) {
    code += chars.charAt(Math.floor(Math.random() * (maxPos + 1)));
  }
  return code;
}
